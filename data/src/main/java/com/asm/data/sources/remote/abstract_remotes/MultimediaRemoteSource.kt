package com.asm.data.sources.remote.abstract_remotes

interface MultimediaRemoteSource {
    suspend fun uploadImage(path: String, imageName: String, byteArray: ByteArray): String
    suspend fun downloadImage(path: String): ByteArray
}