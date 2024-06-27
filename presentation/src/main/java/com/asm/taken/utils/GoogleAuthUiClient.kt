package com.asm.taken.utils

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.asm.taken.R
import com.asm.taken.model.SignInResult
import com.asm.taken.model.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    @ApplicationContext val context: Context,
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) {
    companion object {
        const val TAG: String = "GoogleAuthUiClient"
    }

    suspend fun signInWithGoogle(): SignInResult {
        return try {
            signInWithCredential(true)
        } catch (exception: GetCredentialException ) {
            if (exception !is NoCredentialException) {
                Log.e(TAG, exception.stackTraceToString())
                SignInResult(
                    null,
                    "Credential request fail"
                )
            }
            signInWithCredential()
        } catch (exception: CredentialException) {
            Log.e(TAG, exception.stackTraceToString())
            SignInResult(
                null,
                "Credential handle fail"
            )
        }
    }

    private suspend fun signInWithCredential(authorizedAccounts: Boolean = false): SignInResult {
        val credentialRequest = getCredentialRequest(authorizedAccounts)
        val credentialResponse = credentialManager.getCredential(context, credentialRequest)
        val authCredential = handleCredentialResponse(credentialResponse)
        return signInWithCredential(authCredential)
    }

    private suspend fun signInWithCredential(authCredential: AuthCredential): SignInResult {
        try {
            val firebaseUser = auth.signInWithCredential(authCredential).await().user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return SignInResult(
                    null,
                    "Error to authenticate"
                )
            }
            return SignInResult(
                UserData(
                    firebaseUser.uid,
                    firebaseUser.email,
                    firebaseUser.photoUrl?.toString()
                ),
                null
            )
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            return SignInResult(
                null,
                "Error to authenticate"
            )
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