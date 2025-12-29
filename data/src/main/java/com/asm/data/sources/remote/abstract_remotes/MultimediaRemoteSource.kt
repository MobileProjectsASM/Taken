package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface MultimediaRemoteSource {
    suspend fun uploadResource(path: String, byteArray: ByteArray): Result<String, GeneralError>
    suspend fun getUrlResource(path: String): Result<String?, GeneralError>
    suspend fun deleteResource(path: String): Result<Unit, GeneralError>
    suspend fun deleteResourceByUrl(url: String): Result<Boolean, GeneralError>
}