package com.asm.domain.repositories

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.ProviderId
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface AuthRepository {
    suspend fun authWithEmailAndPassword(email: String, password: String): Result<AuthUser, GeneralError>
    suspend fun authWithToken(token: String, providerId: ProviderId): Result<AuthUser, GeneralError>
    suspend fun authWithOTP(sessionId: String, otp: String): Result<AuthUser, GeneralError>
    suspend fun createAccount(email: String, password: String): Result<Unit, GeneralError>
    suspend fun signOut(): Result<Unit, GeneralError>
}