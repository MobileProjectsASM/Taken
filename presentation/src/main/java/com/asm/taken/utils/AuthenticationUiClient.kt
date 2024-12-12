package com.asm.taken.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.LifecycleCoroutineScope
import com.asm.taken.R
import com.asm.taken.model.AuthError
import com.asm.taken.model.AuthResult
import com.asm.taken.model.SendOtpError
import com.asm.taken.model.SendOtpResult
import com.asm.taken.model.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
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

class AuthenticationUiClient @Inject constructor(
    @ApplicationContext val context: Context,
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {
    companion object {
        const val TAG: String = "GoogleAuthUiClient"
    }

    suspend fun signInWithGoogle(): AuthResult {
        return try {
            signInWithCredential(true)
        } catch (exception: GetCredentialException ) {
            if (exception !is NoCredentialException) {
                Log.e(TAG, exception.stackTraceToString())
                AuthResult.Failure(AuthError.AUTH_ERROR)
            }
            signInWithCredential()
        } catch (exception: CredentialException) {
            Log.e(TAG, exception.stackTraceToString())
            AuthResult.Failure(AuthError.UNKNOWN_ERROR)
        }
    }

    fun authWithPhoneNumber(
        activity: Activity,
        coroutineScope: CoroutineScope,
        phoneNumber: String,
        onOtpSend: (SendOtpResult) -> Unit,
    ) {
        val phoneAuthOptions = getPhoneAuthOptions(
            activity,
            phoneNumber,
            object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d(TAG, "onVerificationCompleted")
                    coroutineScope.launch {
                        val authResult = signInWithCredential(phoneAuthCredential)
                        val sendOtpResult = when (authResult) {
                            is AuthResult.Failure -> {
                                val sendOtpError = when (authResult.authError) {
                                    AuthError.AUTH_ERROR -> SendOtpError.AUTH_ERROR
                                    AuthError.UNKNOWN_ERROR -> SendOtpError.UNKNOWN_ERROR
                                }
                                SendOtpResult.Failure(sendOtpError)
                            }
                            is AuthResult.PhoneCodeSent -> SendOtpResult.SentOtp(authResult.verificationId)
                            is AuthResult.Successful -> SendOtpResult.AutomaticAuthWithPhone(authResult.userData)
                        }
                        onOtpSend(sendOtpResult)
                    }
                }

                override fun onVerificationFailed(firebaseException: FirebaseException) {
                    Log.e(TAG, firebaseException.stackTraceToString())
                    val sendOtpResult = SendOtpResult.Failure( when (firebaseException) {
                        is FirebaseAuthInvalidCredentialsException, is FirebaseTooManyRequestsException, is FirebaseAuthMissingActivityForRecaptchaException -> SendOtpError.SEND_OTP_ERROR
                        else -> SendOtpError.UNKNOWN_ERROR
                    })
                    onOtpSend(sendOtpResult)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "onCodeSent:$verificationId")
                    val sendOtpResult = SendOtpResult.SentOtp(verificationId)
                    onOtpSend(sendOtpResult)
                }
            }
        )
        onOtpSend(SendOtpResult.Loading)
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    suspend fun manualSignInPhoneNumber(verificationId: String, code: String): AuthResult {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)
        return signInWithCredential(phoneAuthCredential)
    }

    private suspend fun signInWithCredential(authorizedAccounts: Boolean = false): AuthResult {
        val credentialRequest = getCredentialRequest(authorizedAccounts)
        val credentialResponse = credentialManager.getCredential(context, credentialRequest)
        val authCredential = handleCredentialResponse(credentialResponse)
        return signInWithCredential(authCredential)
    }

    private suspend fun signInWithCredential(authCredential: AuthCredential): AuthResult {
        try {
            val firebaseUser = auth.signInWithCredential(authCredential).await().user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return AuthResult.Failure(AuthError.UNKNOWN_ERROR)
            }
            return AuthResult.Successful(
                UserData(
                    firebaseUser.uid,
                    firebaseUser.email,
                    firebaseUser.photoUrl?.toString()
                )
            )
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            return AuthResult.Failure(AuthError.AUTH_ERROR)
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
        }
    }

    private fun getCredentialRequest(authorizedAccounts: Boolean): GetCredentialRequest {
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
                        throw CredentialException("Receive an invalid google id token response")
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