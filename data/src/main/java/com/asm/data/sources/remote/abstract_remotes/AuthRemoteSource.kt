package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.AuthProcessException
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError
import com.google.firebase.auth.AuthCredential
import kotlin.jvm.Throws

interface AuthRemoteSource {
    @Throws(AuthProcessException::class)
    suspend fun authWithEmailAndPassword(email: String, password: String): Result<AuthUser, Failure>
    @Throws(AuthProcessException::class)
    suspend fun authWithToken(token: String, providerId: String): Result<AuthUser, Failure>
    @Throws(AuthProcessException::class)
    suspend fun authWithOtp(sessionId: String, otp: String): Result<AuthUser, Failure>
    suspend fun authWithCredential(authCredential: AuthCredential): Result<AuthUser, GeneralError> //Exception case
    suspend fun createAccount(email: String, password: String): Result<Unit, GeneralError>
    suspend fun getAuthUser(): Result<AuthUser, GeneralError>
    suspend fun signOut(): Result<Unit, GeneralError>
}