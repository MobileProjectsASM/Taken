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
import androidx.credentials.exceptions.NoCredentialException
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.taken.R
import com.asm.taken.model.AuthError
import com.asm.taken.model.SendOtpError
import com.asm.taken.model.SignUpError
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthenticationClient @Inject constructor(
    @ApplicationContext val context: Context,
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {
    companion object {
        const val TAG: String = "AuthenticationUiClient"
    }

    suspend fun createAccount(email: String, password: String): SignUpResult {
        return try {
            val firebaseUser = auth.createUserWithEmailAndPassword(email, password).await().user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return SignUpResult.Failure(SignUpError.UNKNOWN_ERROR)
            }
            SignUpResult.Successful
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            if (exception is FirebaseException) {
                val signUpError: SignUpError = when {
                    exception is FirebaseNetworkException -> SignUpError.NETWORK_CONNECTION
                    exception is FirebaseAuthException -> when (exception.errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE", "email-already-in-use" -> SignUpError.EMAIL_ALREADY_IN_USE
                        "ERROR_INVALID_EMAIL", "invalid-email" -> SignUpError.INVALID_EMAIL
                        "ERROR_WEAK_PASSWORD", "weak-password" -> SignUpError.WEAK_PASSWORD
                        "ERROR_NETWORK_REQUEST_FAILED", "network-request-failed" -> SignUpError.NETWORK_CONNECTION
                        else -> SignUpError.UNKNOWN_ERROR
                    }
                    else -> SignUpError.UNKNOWN_ERROR
                }
                return SignUpResult.Failure(signUpError)
            } else {
                SignUpResult.Failure(SignUpError.UNKNOWN_ERROR)
            }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            val firebaseUser = auth.signInWithEmailAndPassword(email, password).await().user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
            AuthResult.Successful(
                userData = UserData(
                    userId = firebaseUser.uid,
                    profilePictureUrl = firebaseUser.photoUrl?.toString()
                )
            )
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            when {
                exception is FirebaseException -> {
                    val authError: AuthError = when {
                        exception is FirebaseNetworkException -> AuthError.NETWORK_CONNECTION
                        exception is FirebaseAuthException -> when (exception.errorCode) {
                            "ERROR_INVALID_EMAIL" -> AuthError.ERROR_INVALID_EMAIL
                            "ERROR_INVALID_CREDENTIAL" -> AuthError.ERROR_WRONG_PASSWORD
                            "ERROR_USER_NOT_FOUND" -> AuthError.ERROR_USER_NOT_FOUND
                            "ERROR_INVALID_LOGIN_CREDENTIALS" -> AuthError.ERROR_INVALID_LOGIN_CREDENTIALS
                            "ERROR_USER_DISABLED" -> AuthError.ERROR_USER_DISABLED
                            "ERROR_TOO_MANY_REQUESTS" -> AuthError.ERROR_TOO_MANY_REQUESTS
                            "ERROR_NETWORK_REQUEST_FAILED" -> AuthError.NETWORK_CONNECTION
                            else -> AuthError.UNKNOWN_ERROR
                        }
                        else -> AuthError.UNKNOWN_ERROR
                    }
                    return AuthResult.Failure(authError)
                }
                else -> AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
        }
    }

    fun signInWithFacebook(
        activityResultRegistryOwner: ActivityResultRegistryOwner,
        coroutineScope: CoroutineScope,
        onAuthResult: (AuthResult) -> Unit
    ) {
        val callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, error.stackTraceToString())
                onAuthResult(AuthResult.Failure(AuthError.UNKNOWN_ERROR))
            }

            override fun onSuccess(result: LoginResult) {
                coroutineScope.launch {
                    onAuthResult(AuthResult.Loading)
                    val accessToken = result.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(accessToken)
                    val authResult = signInWithCredential(credential)
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

    suspend fun signInWithGoogle(): AuthResult {
        return try {
            signInWithCredential(true)
        } catch (exception: GetCredentialException) {
            if (exception !is NoCredentialException) {
                Log.e(TAG, exception.stackTraceToString())
                return AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
            try {
                signInWithCredential()
            } catch (exception: Exception) {
                Log.e(TAG, exception.stackTraceToString())
                AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            AuthResult.Failure(AuthError.UNKNOWN_ERROR)
        }
    }

    fun authWithPhoneNumber(
        activity: Activity,
        coroutineScope: CoroutineScope,
        phoneNumber: String,
        onOtpSend: (SendOtpResult) -> Unit,
        onAuthResult: (AuthResult) -> Unit
    ) {
        val phoneAuthOptions = getPhoneAuthOptions(
            activity,
            phoneNumber,
            object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted")
                    coroutineScope.launch {
                        val authResult = signInWithCredential(phoneAuthCredential)
                        onAuthResult(authResult)
                    }
                }

                override fun onVerificationFailed(firebaseException: FirebaseException) {
                    Log.e(TAG, firebaseException.stackTraceToString())
                    val phonesSendOtpError = when (firebaseException) {
                        is FirebaseAuthInvalidCredentialsException -> SendOtpError.PHONE_NUMBER_INVALID_ERROR
                        is FirebaseNetworkException -> SendOtpError.NETWORK_CONNECTION
                        is FirebaseTooManyRequestsException, is FirebaseAuthMissingActivityForRecaptchaException -> SendOtpError.SERVER_ERROR
                        else -> SendOtpError.UNKNOWN_ERROR
                    }
                    val sendOtpResult = SendOtpResult.Failure(phonesSendOtpError)
                    onOtpSend(sendOtpResult)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "onCodeSent:$verificationId")
                    val sendOtpResult = SendOtpResult.SentOtp(verificationId, phoneNumber)
                    onOtpSend(sendOtpResult)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {

                }
            }
        )
        onOtpSend(SendOtpResult.Loading)
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    suspend fun verifyOtp(verificationId: String, otp: String): AuthResult {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp)
        return signInWithCredential(phoneAuthCredential)
    }

    private suspend fun signInWithCredential(authorizedAccounts: Boolean = false): AuthResult {
        val credentialRequest = buildCredentialRequest(authorizedAccounts)
        val credentialResponse = credentialManager.getCredential(context, credentialRequest)
        val authCredential = handleCredentialResponse(credentialResponse)
        return signInWithCredential(authCredential)
    }

    private suspend fun signInWithCredential(authCredential: AuthCredential): AuthResult {
        return try {
            val firebaseUser = auth.signInWithCredential(authCredential).await().user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
            val photoUrl = firebaseUser.photoUrl?.toString()?.let { baseUrl ->
                AccessToken.getCurrentAccessToken()?.token?.let { "$baseUrl?access_token=$it" }
            }
            AuthResult.Successful(
                UserData(
                    firebaseUser.uid,
                    photoUrl
                )
            )
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            if (exception is FirebaseException) {
                if (exception is FirebaseNetworkException) {
                    AuthResult.Failure(AuthError.NETWORK_CONNECTION)
                } else {
                    AuthResult.Failure(AuthError.UNKNOWN_ERROR)
                }
            } else {
                AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
        }
    }

    fun getCurrentUserSignedIn(): UserData? {
        return auth.currentUser?.let {
            UserData(
                userId = auth.currentUser!!.uid,
                profilePictureUrl = auth.currentUser?.photoUrl.toString()
            )
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
            .setServerClientId(context.getString(R.string.default_web_client_id))
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
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        return GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    } catch (exception: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Receive an invalid google id token response", exception)
                        throw exception
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                    throw CredentialException("Unexpected type of credential")
                }
            }
            else -> {
                Log.e(TAG, "Unexpected type of credential")
                throw CredentialException("Unexpected type of credential")
            }
        }
    }
}

class CredentialException(message: String): Exception(message)

//region Authentication

sealed class SendOtpResult {
    data object Loading: SendOtpResult()
    data class SentOtp(val verificationId: String, val phoneNumber: String): SendOtpResult()
    data class Failure(val phonesSendOtpError: SendOtpError): SendOtpResult()
}

sealed class AuthResult {
    data object Loading: AuthResult()
    data class Successful(val userData: UserData): AuthResult()
    data class Failure(val authError: AuthError): AuthResult()
}

enum class LogoutResult {
    SUCCESSFUL,
    FAILURE
}

sealed class SignUpResult {
    data object Loading: SignUpResult()
    data object Successful: SignUpResult()
    data class Failure(val signUpError: SignUpError): SignUpResult()
}

data class UserData(
    val userId: String,
    val profilePictureUrl: String?
)

//endregion