package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure

interface MultimediaRepository {
    suspend fun uploadUserImage(userId: String, profileImageName: String, base64: String): Result<String, GeneralFailure>
    suspend fun getDefaultUserImage(): Result<String, GeneralFailure>
}