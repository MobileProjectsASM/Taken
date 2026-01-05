package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.AuthRemoteSource
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.ProviderId
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthFirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRemoteSource {

    companion object {
        const val TAG = "auth_firebase_source"
    }

    override suspend fun authWithEmailAndPassword(
        email: String,
        password: String
    ): Result<AuthUser, GeneralError> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            Result.Successful(AuthUser(firebaseUser.uid, firebaseUser.photoUrl?.toString()))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to sign in with email and password", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.NetworkError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun authWithToken(token: String, providerId: ProviderId): Result<AuthUser, GeneralError> {
        return try {
            val authCredential: AuthCredential = when (providerId) {
                ProviderId.FACEBOOK -> FacebookAuthProvider.getCredential(token)
                ProviderId.GOOGLE -> GoogleAuthProvider.getCredential(token, null)
            }
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            val photoUrl = firebaseUser.photoUrl?.toString()?.let { baseUrl ->
                if (providerId == ProviderId.FACEBOOK) "$baseUrl?height=500&access_token=$token"
                else baseUrl
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

    override suspend fun createAccount(
        email: String,
        password: String
    ): Result<Unit, GeneralError> {
        return try {
            val createAccountResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
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
                is FirebaseNetworkException -> GeneralError.NetworkError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }
}