package com.asm.data.sources.remote.abstract_remotes

interface MultimediaRemoteSource {
    suspend fun uploadResource(path: String, byteArray: ByteArray): String
    suspend fun getUrlResource(path: String): String
}