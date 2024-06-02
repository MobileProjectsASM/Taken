package com.asm.domain.repositories

import com.asm.domain.entities.Result

interface MultimediaRepository {
    suspend fun uploadUserImage(userId: String, profileImageName: String, base64: String): Result<String>
    suspend fun getDefaultUserImage(): Result<String>
}