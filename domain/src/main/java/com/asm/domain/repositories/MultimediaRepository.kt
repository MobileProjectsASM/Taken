package com.asm.domain.repositories

import com.asm.domain.errors.Failure
import com.asm.domain.utils.Either

interface MultimediaRepository {
    suspend fun uploadUserImage(userId: String, profileImageName: String, base64: String): Either<Failure, String>
    suspend fun getDefaultUserImage(): Either<Failure, String>
}