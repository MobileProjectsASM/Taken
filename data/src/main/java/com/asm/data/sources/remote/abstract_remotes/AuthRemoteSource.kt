package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.ProviderId
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.google.firebase.auth.AuthCredential

interface AuthRemoteSource {
    suspend fun authWithEmailAndPassword(email: String, password: String): Result<AuthUser, GeneralError>
    suspend fun authWithToken(token: String, providerId: ProviderId): Result<AuthUser, GeneralError>
    suspend fun authWithOtp(sessionId: String, otp: String): Result<AuthUser, GeneralError>
    suspend fun authWithCredential(authCredential: AuthCredential): Result<AuthUser, GeneralError> //Exception case
    suspend fun createAccount(email: String, password: String): Result<Unit, GeneralError>
    suspend fun signOut(): Result<Unit, GeneralError>
}