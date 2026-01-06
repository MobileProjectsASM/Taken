package com.asm.taken.utils

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.facebook.AccessToken
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthenticationClient @Inject constructor(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager,
) {
    companion object {
        const val TAG: String = "AuthenticationClient"
    }

    fun getCurrentUserSocialNetworkImage(): Result<String?, GeneralError> = try {
        val socialNetworkImage = auth.currentUser?.photoUrl?.toString()?.let { baseUrl ->
            AccessToken.getCurrentAccessToken()?.token?.let { "$baseUrl?access_token=$it" }
        }
        Result.Successful(socialNetworkImage)
    } catch (exception: Exception) {
        Result.Unsuccessful(GeneralError.Unknown)
    }

    fun validateOtp(
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
                        is FirebaseAuthInvalidCredentialsException, is FirebaseTooManyRequestsException, is FirebaseAuthMissingActivityForRecaptchaException -> GeneralError.ClientError()
                            .toUnsuccessful()

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
}