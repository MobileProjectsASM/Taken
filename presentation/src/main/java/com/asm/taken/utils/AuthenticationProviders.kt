package com.asm.taken.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.asm.data.sources.remote.abstract_remotes.AuthRemoteSource
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.taken.R
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class AuthenticationProviders(
    private val firebaseAuth: FirebaseAuth,
    private val authRemoteSource: AuthRemoteSource,
    private val context: Context,
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager,
    private val metaAuthLauncher: ActivityResultLauncher<Collection<String>>,
) {

    companion object {
        const val TAG: String = "auth_providers"
    }

    sealed class AuthWithPhoneResult {
        data class OtpSend(val verificationId: String) : AuthWithPhoneResult()
        data class AuthenticatedWithCredential(val authCredential: PhoneAuthCredential) :
            AuthWithPhoneResult()

        data class Error(val generalError: GeneralError) : AuthWithPhoneResult()
    }

    sealed class AuthWithPhone {
        data class OtpSend(val verificationId: String) : AuthWithPhone()
        data class Authenticated(val authUser: AuthUser): AuthWithPhone()
        data class Error(val generalError: GeneralError) : AuthWithPhone()
    }

    suspend fun authWithGoogle(): Result<String, GeneralError> =
        signInWithCredentialManager(true)

    suspend fun authWithFacebook(): Result<String, GeneralError> {
        val authWithFacebookResult = suspendCancellableCoroutine {
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
                val token = authWithFacebookResult.data.accessToken.token
                Result.Successful(token)
            }

            is Result.Unsuccessful<GeneralError> -> authWithFacebookResult
        }
    }

    suspend fun authWithPhoneNumber(
        activity: Activity,
        phoneNumber: String,
    ): AuthWithPhone {
        val authWithPhoneResult: AuthWithPhoneResult = suspendCancellableCoroutine {
            val callback = object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted")
                    it.resume(AuthWithPhoneResult.AuthenticatedWithCredential(phoneAuthCredential))
                }

                override fun onVerificationFailed(firebaseException: FirebaseException) {
                    Log.e(TAG, "Unexpected Exception to auth with phone number", firebaseException)
                    val phonesSendOtpError = when (firebaseException) {
                        is FirebaseAuthInvalidCredentialsException, is FirebaseTooManyRequestsException, is FirebaseAuthMissingActivityForRecaptchaException -> GeneralError.ClientError()
                        is FirebaseNetworkException -> GeneralError.NetworkError
                        else -> GeneralError.Unknown
                    }
                    it.resume(AuthWithPhoneResult.Error(phonesSendOtpError))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d(TAG, "onCodeSent")
                    val credential = PhoneAuthProvider.getCredential(verificationId, "123456")

                    onVerificationCompleted(credential)
                    //it.resume(AuthWithPhoneResult.OtpSend(verificationId))
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {

                }
            }

            val phoneAuthOptions = getPhoneAuthOptions(activity, phoneNumber, callback)

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)

            it.invokeOnCancellation {

            }
        }
        return when (authWithPhoneResult) {
            is AuthWithPhoneResult.AuthenticatedWithCredential -> withContext(Dispatchers.IO) {
                val authResult = authRemoteSource.authWithCredential(authWithPhoneResult.authCredential)
                when (authResult) {
                    is Result.Successful<AuthUser> -> AuthWithPhone.Authenticated(authResult.data)
                    is Result.Unsuccessful<GeneralError> -> AuthWithPhone.Error(authResult.error)
                }
            }
            is AuthWithPhoneResult.Error -> AuthWithPhone.Error(authWithPhoneResult.generalError)
            is AuthWithPhoneResult.OtpSend -> AuthWithPhone.OtpSend(authWithPhoneResult.verificationId)
        }
    }

    private suspend fun signInWithCredentialManager(
        authorizedAccounts: Boolean
    ): Result<String, GeneralError> {
        return try {
            val credentialRequest = buildCredentialRequest(authorizedAccounts)
            val credentialResponse =
                CredentialManager.create(context).getCredential(context, credentialRequest)
            try {
                val token = handleCredentialResponse(credentialResponse)
                Result.Successful(token)
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

    private fun handleCredentialResponse(credentialResponse: GetCredentialResponse): String {
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                val credentialType = credential.type
                if (credentialType == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    return googleIdTokenCredential.idToken
                } else {
                    throw Exception("Unexpected type of credential")
                }
            }

            else -> {
                throw Exception("Unexpected type of credential")
            }
        }
    }

    private fun getPhoneAuthOptions(
        activity: Activity,
        phoneNumber: String,
        onVerificationStateChangedCallbacks: OnVerificationStateChangedCallbacks
    ): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationStateChangedCallbacks)
            .build()
    }
}