package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import java.io.InputStream

interface MultimediaRepository {
    suspend fun uploadUserImage(userId: String, profileImageName: String, byteArray: ByteArray): Result<String, GeneralFailure>
    suspend fun getDefaultUserImage(): Result<String, GeneralFailure>
}