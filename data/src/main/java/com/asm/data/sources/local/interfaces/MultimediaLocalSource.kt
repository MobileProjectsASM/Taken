package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface MultimediaLocalSource {
    suspend fun getFileContent(path: String): Result<ByteArray, GeneralError>
    suspend fun saveImage(folderPath: String, imageName: String, byteArray: ByteArray): Result<String, GeneralError>
    suspend fun existsImage(path: String): Result<Boolean, GeneralError>
}