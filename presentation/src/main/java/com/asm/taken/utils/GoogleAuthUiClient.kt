package com.asm.taken.utils

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.asm.taken.R
import com.asm.taken.model.SignInResult
import com.asm.taken.model.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    @ApplicationContext val context: Context,
    private val auth: FirebaseAuth,
    private val signInClient: SignInClient
) {
    companion object {
        const val TAG: String = "GoogleAuthUiClient"
    }

    suspend fun getSignInIntent(): IntentSender? {
        val result = try {
            signInClient.beginSignIn(buildSignInRequest()).await()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        try {
            val signInCredential = signInClient.getSignInCredentialFromIntent(intent)
            val idToken = signInCredential.googleIdToken
            if (idToken == null) {
                Log.e(TAG, "Token is null")
                return SignInResult(
                    null,
                    "Error to authenticate"
                )
            }
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val firebaseUser = auth.signInWithCredential(firebaseCredential).await().user
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
            signInClient.signOut().await()
            auth.signOut()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions
                .builder()
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build()
        )
        .build()
}