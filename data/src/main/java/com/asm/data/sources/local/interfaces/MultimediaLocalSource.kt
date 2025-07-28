package com.asm.data.sources.local.interfaces

interface MultimediaLocalSource {
    suspend fun saveImage(folderPath: String, imageName: String, byteArray: ByteArray): String
    suspend fun existsImage(path: String): Boolean
}