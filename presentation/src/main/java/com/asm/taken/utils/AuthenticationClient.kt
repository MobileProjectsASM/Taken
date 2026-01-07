package com.asm.taken.utils

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
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
            AccessToken.getCurrentAccessToken()?.token?.let { "$baseUrl?height=500&access_token=$it" }
                ?: baseUrl
        }
        Result.Successful(socialNetworkImage)
    } catch (exception: Exception) {
        Result.Unsuccessful(GeneralError.Unknown)
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

}