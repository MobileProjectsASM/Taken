package com.asm.data.sources.remote.impl

import android.util.Base64
import android.util.Log
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MultimediaStorageSource @Inject constructor(
    val storageReference: StorageReference
): MultimediaRemoteSource {

    companion object {
        const val TAG = "MULTIMEDIA_STORAGE_SOURCE"
        const val MAX_DOWNLOAD_BYTES = 1_024L * 1_024L
    }

    override suspend fun uploadImage(path: String, imageName: String, base64: String): String {
        try {
            val imageReference = storageReference.child("$path/$imageName")
            val bytes = base64.toByteArray()
            imageReference.putBytes(bytes).await()
            val imageUri = imageReference.downloadUrl.await()
            return imageUri.path ?: throw Exception("Uri invalid")
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }

    override suspend fun downloadImage(path: String): String {
        try {
            val imageReference = storageReference.child(path)
            val bytes = imageReference.getBytes(MAX_DOWNLOAD_BYTES).await()
            return bytes.toBase64()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)
}