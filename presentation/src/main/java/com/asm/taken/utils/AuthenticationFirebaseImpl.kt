package com.asm.taken.utils

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.AuthRepository
import com.asm.taken.R
import com.asm.taken.utils.AuthenticationClient.Companion.TAG
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.resume

class AuthenticationFirebaseImpl(
    private val context: Context,
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager,
    private val metaAuthLauncher: ActivityResultLauncher<Collection<String>>,
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun authWithEmailAndPassword(
        email: String,
        password: String
    ): Result<AuthUser, GeneralError> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            Result.Successful(AuthUser(firebaseUser.uid, firebaseUser.photoUrl?.toString()))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected Exception to sign in with email and password", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.NetworkError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun authWithGoogle(): Result<AuthUser, GeneralError> =
        signInWithCredentialManager(true)

    override suspend fun authWithFacebook(): Result<AuthUser, GeneralError> {
        val authWithFacebookResult =
            suspendCancellableCoroutine {
                val callback = object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.e(TAG, "Unexpected Error cancel process")
                        it.resume(GeneralError.ClientError().toUnsuccessful())
                    }

                    override fun onError(error: FacebookException) {
                        Log.e(TAG, "Unexpected Exception to sign in with facebook", error)
                        it.resume(GeneralError.ServerError().toUnsuccessful())
                    }

                    override fun onSuccess(result: LoginResult) {
                        it.resume(Result.Successful(result))
                    }
                }

                loginManager.registerCallback(callbackManager, callback)

                metaAuthLauncher.launch(listOf("email", "public_profile"))

                it.invokeOnCancellation {
                    loginManager.unregisterCallback(callbackManager)
                }
            }
        return when (authWithFacebookResult) {
            is Result.Successful<LoginResult> -> {
                val accessToken = authWithFacebookResult.data.accessToken.token
                val credential = FacebookAuthProvider.getCredential(accessToken)
                signInWithFirebase(credential)
            }
            is Result.Unsuccessful<GeneralError> -> authWithFacebookResult
        }
    }

    private suspend fun signInWithCredentialManager(
        authorizedAccounts: Boolean
    ): Result<AuthUser, GeneralError> {
        return try {
            val credentialRequest = buildCredentialRequest(authorizedAccounts)
            val credentialResponse = CredentialManager.create(context).getCredential(context, credentialRequest)
            try {
                val authCredential = handleCredentialResponse(credentialResponse)
                signInWithFirebase(authCredential)
            } catch (exception: Exception) {
                Log.e(TAG, "Unexpected exception to handle credentials", exception)
                GeneralError.ClientError().toUnsuccessful()
            }
        } catch (exception: GetCredentialException) {
            Log.e(TAG, "Unexpected exception to get credentials", exception)
            if (authorizedAccounts) signInWithCredentialManager(false)
            else GeneralError.ClientError().toUnsuccessful()
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to get credentials", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private fun buildCredentialRequest(authorizedAccounts: Boolean): GetCredentialRequest {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> "$str${"%02x".format(it)}" }
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(authorizedAccounts)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun handleCredentialResponse(credentialResponse: GetCredentialResponse): AuthCredential {
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                val credentialType = credential.type
                if (credentialType == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    return GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                } else {
                    throw Exception("Unexpected type of credential")
                }
            }

            else -> {
                throw Exception("Unexpected type of credential")
            }
        }
    }

    private suspend fun signInWithFirebase(authCredential: AuthCredential): Result<AuthUser, GeneralError> {
        return try {
            val authResult = auth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            val photoUrl = firebaseUser.photoUrl?.toString()?.let { baseUrl ->
                AccessToken.getCurrentAccessToken()?.token?.let { "$baseUrl?access_token=$it" }
            }
            Result.Successful(AuthUser(firebaseUser.uid, photoUrl))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to sign in with firebase", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.NetworkError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }
}