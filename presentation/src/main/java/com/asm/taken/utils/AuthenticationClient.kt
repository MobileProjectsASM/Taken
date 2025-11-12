package com.asm.taken.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.taken.R
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
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthenticationClient @Inject constructor(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    private val resourceResolver: ResourceResolver,
) {
    companion object {
        const val TAG: String = "AuthenticationUiClient"
    }

    suspend fun createAccount(email: String, password: String): Result<Unit, GeneralError> {
        return try {
            val createAccountResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = createAccountResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to create account", exception)
            when (exception) {
                is FirebaseAuthException -> GeneralError.ClientError("")
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<AuthUser, GeneralError> {
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
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    fun signInWithFacebook(
        activityResultRegistryOwner: ActivityResultRegistryOwner,
        coroutineScope: CoroutineScope,
        onFacebookLoginLoading: () -> Unit,
        onAuthResult: (Result<AuthUser, GeneralError>) -> Unit
    ) {
        val callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.e(TAG, "Unexpected Error cancel process")
                    onAuthResult(GeneralError.ClientError().toUnsuccessful())
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "Unexpected Exception to sign in with facebook", error)
                    onAuthResult(GeneralError.ServerError().toUnsuccessful())
                }

                override fun onSuccess(result: LoginResult) {
                    coroutineScope.launch {
                        onFacebookLoginLoading()
                        val accessToken = result.accessToken.token
                        val credential = FacebookAuthProvider.getCredential(accessToken)
                        val authResult = signInWithFirebase(credential)
                        onAuthResult(authResult)
                    }
                }
            })
        LoginManager.getInstance().logInWithReadPermissions(
            activityResultRegistryOwner,
            callbackManager,
            listOf("email", "public_profile")
        )
    }

    suspend fun signInWithGoogle(context: Context): Result<AuthUser, GeneralError> =
        signInWithCredentialManager(context, true)

    fun authWithPhoneNumber(
        activity: Activity,
        coroutineScope: CoroutineScope,
        phoneNumber: String,
        onPhoneLoginLoading: () -> Unit,
        onOtpSend: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onAuthResult: (Result<AuthUser, GeneralError>) -> Unit
    ) {
        val phoneAuthOptions = getPhoneAuthOptions(
            activity,
            phoneNumber,
            object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted")
                    coroutineScope.launch {
                        val authResult = signInWithFirebase(phoneAuthCredential)
                        onAuthResult(authResult)
                    }
                }

                override fun onVerificationFailed(firebaseException: FirebaseException) {
                    Log.e(TAG, "Unexpected Exception to auth with phone number", firebaseException)
                    val phonesSendOtpError = when (firebaseException) {
                        is FirebaseAuthInvalidCredentialsException, is FirebaseTooManyRequestsException, is FirebaseAuthMissingActivityForRecaptchaException -> GeneralError.ClientError().toUnsuccessful()
                        is FirebaseNetworkException -> GeneralError.NetworkError.toUnsuccessful()
                        else -> GeneralError.Unknown.toUnsuccessful()
                    }
                    onAuthResult(phonesSendOtpError)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d(TAG, "onCodeSent")
                    onOtpSend(verificationId, token)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {

                }
            }
        )
        onPhoneLoginLoading()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    suspend fun verifyOtp(verificationId: String, otp: String): Result<AuthUser, GeneralError> {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp)
        return signInWithFirebase(phoneAuthCredential)
    }

    private suspend fun signInWithCredentialManager(
        context: Context,
        authorizedAccounts: Boolean
    ): Result<AuthUser, GeneralError> {
        return try {
            val credentialRequest = buildCredentialRequest(authorizedAccounts)
            val credentialResponse = credentialManager.getCredential(context, credentialRequest)
            try {
                val authCredential = handleCredentialResponse(credentialResponse)
                signInWithFirebase(authCredential)
            } catch (exception: Exception) {
                Log.e(TAG, "Unexpected exception to handle credentials", exception)
                GeneralError.ClientError().toUnsuccessful()
            }
        } catch (exception: GetCredentialException) {
            Log.e(TAG, "Unexpected exception to get credentials", exception)
            if (authorizedAccounts) signInWithCredentialManager(context, false)
            else GeneralError.ClientError().toUnsuccessful()
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to get credentials", exception)
            GeneralError.Unknown.toUnsuccessful()
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
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    suspend fun signOut(): Result<Unit, GeneralError> {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            return Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            return GeneralError.Unknown.toUnsuccessful()
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
            .setServerClientId(resourceResolver.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun getPhoneAuthOptions(
        activity: Activity,
        phoneNumber: String,
        onVerificationStateChangedCallbacks: OnVerificationStateChangedCallbacks
    ): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationStateChangedCallbacks)
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
}