package com.asm.data.sources.local.interfaces

interface MultimediaLocalSource {
    suspend fun saveImage(folderPath: String, imageName: String, base64: String): String
    suspend fun existsImage(path: String): Boolean
}