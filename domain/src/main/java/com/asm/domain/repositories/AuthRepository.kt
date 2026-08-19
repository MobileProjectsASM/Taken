package com.asm.domain.repositories

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError

interface AuthRepository {
    suspend fun authWithEmailAndPassword(email: String, password: String): Result<AuthUser, Failure>
    suspend fun authWithToken(token: String, providerId: String): Result<AuthUser, Failure>
    suspend fun authWithOTP(sessionId: String, otp: String): Result<AuthUser, Failure>
    suspend fun createAccount(email: String, password: String): Result<Unit, GeneralError>
    suspend fun getAuthUser(): Result<AuthUser, GeneralError>
    suspend fun signOut(): Result<Unit, GeneralError>
}