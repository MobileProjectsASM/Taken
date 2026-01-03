package com.asm.domain.repositories

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface AuthRepository {
    suspend fun authWithEmailAndPassword(email: String, password: String): Result<AuthUser, GeneralError>
    suspend fun authWithGoogle(): Result<AuthUser, GeneralError>
    suspend fun authWithFacebook(): Result<AuthUser, GeneralError>
}