package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
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
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthFirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager
) : AuthRemoteSource {

    companion object {
        const val TAG = "auth_firebase_source"

        enum class Provider {
            UNDEFINED,
            FACEBOOK,
            PASSWORD,
            GOOGLE,
            PHONE;

            companion object {
                fun getType(id: String): Provider = when (id) {
                    "facebook.com" -> FACEBOOK
                    "password" -> PASSWORD
                    "google.com" -> GOOGLE
                    "phone" -> PHONE
                    else -> UNDEFINED
                }
            }
        }
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
                is FirebaseNetworkException -> GeneralError.ConnectionError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun authWithToken(
        token: String,
        providerId: String
    ): Result<AuthUser, GeneralError> {
        return try {
            val authCredential = getCredential(providerId) {
                return@getCredential when (providerId) {
                    FacebookAuthProvider.PROVIDER_ID -> setAccessToken(token)
                    GoogleAuthProvider.PROVIDER_ID -> setIdToken(token)
                    else -> throw Exception()
                }
            }
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            val photoUrl = firebaseUser.photoUrl?.toString()?.let { baseUrl ->
                if (providerId == FacebookAuthProvider.PROVIDER_ID) "$baseUrl?height=500&access_token=$token"
                else baseUrl
            }
            Result.Successful(AuthUser(firebaseUser.uid, photoUrl))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to sign in with token", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.ConnectionError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun authWithOtp(
        sessionId: String,
        otp: String
    ): Result<AuthUser, GeneralError> {
        return try {
            val phoneAuthCredential = PhoneAuthProvider.getCredential(sessionId, otp)
            val authResult = firebaseAuth.signInWithCredential(phoneAuthCredential).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            Result.Successful(AuthUser(firebaseUser.uid, null))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to sign in with otp", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.ConnectionError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun authWithCredential(authCredential: AuthCredential): Result<AuthUser, GeneralError> {
        return try {
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user
            if (firebaseUser == null) {
                Log.e(TAG, "FirebaseUser is null")
                return GeneralError.ServerError().toUnsuccessful()
            }
            Result.Successful(AuthUser(firebaseUser.uid, null))
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception to sign in with otp", exception)
            when (exception) {
                is FirebaseAuthInvalidUserException -> GeneralError.ClientError("")
                is FirebaseAuthInvalidCredentialsException -> GeneralError.ClientError("")
                is FirebaseNetworkException -> GeneralError.ConnectionError
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
            val createAccountResult =
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
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
                is FirebaseNetworkException -> GeneralError.ConnectionError
                is FirebaseException -> GeneralError.ClientError("")
                else -> GeneralError.Unknown
            }.toUnsuccessful()
        }
    }

    override suspend fun getAuthUser(): Result<AuthUser, GeneralError> {
        return try {
            firebaseAuth.currentUser?.let {
                val provider = Provider.getType(it.providerData[1].providerId)
                val imageUrl = when (provider) {
                    Provider.FACEBOOK -> {
                        val tokenResult: GetTokenResult? = it.getIdToken(false).await()
                        it.photoUrl?.toString()?.let { photoUrl ->
                            tokenResult?.token?.let { token ->
                                "$photoUrl?height=500&access_token=$token"
                            }
                        }
                    }

                    Provider.GOOGLE -> it.photoUrl.toString()
                    Provider.PASSWORD, Provider.PHONE, Provider.UNDEFINED -> null
                }
                Result.Successful(
                    AuthUser(
                        userId = it.uid,
                        profilePictureUrl = imageUrl
                    )
                )
            } ?: throw Exception("Current user is null")
        } catch (exception: Exception) {
            Log.e(TAG, "unexpected exception to get authenticated user", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun signOut(): Result<Unit, GeneralError> {
        return try {
            firebaseAuth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            return Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, "unexpected error to sign out", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private fun getCredential(
        providerId: String,
        setToken: OAuthProvider.CredentialBuilder.() -> OAuthProvider.CredentialBuilder
    ): AuthCredential {
        return OAuthProvider.newCredentialBuilder(providerId)
            .setToken()
            .build()
    }
}