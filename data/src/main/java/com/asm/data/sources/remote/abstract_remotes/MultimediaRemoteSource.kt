package com.asm.data.sources.remote.abstract_remotes

interface MultimediaRemoteSource {
    suspend fun uploadImage(path: String, imageName: String, base64: String): String
    suspend fun downloadImage(path: String): String
}