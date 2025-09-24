package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface MultimediaRepository {
    suspend fun uploadUserImage(userId: String, profileImageName: String, byteArray: ByteArray): Result<String, GeneralError>
    suspend fun getDefaultUserImage(): Result<String?, GeneralError>
}