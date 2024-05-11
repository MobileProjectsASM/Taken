package com.asm.data.sources.remote.interfaces

interface MultimediaRemoteSource {
    suspend fun uploadImage(path: String, imageName: String, base64: String): String
    suspend fun downloadImage(path: String): String
}