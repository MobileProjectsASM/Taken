package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MultimediaStorageSource @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): MultimediaRemoteSource {

    companion object {
        const val TAG = "MULTIMEDIA_STORAGE_SOURCE"
        const val MAX_DOWNLOAD_BYTES = 1_024L * 1_024L
    }

    override suspend fun uploadImage(path: String, imageName: String, byteArray: ByteArray): String {
        try {
            val imageReference = firebaseStorage.reference.child("$path/$imageName")
            imageReference.putBytes(byteArray).await()
            val imageUri = imageReference.downloadUrl.await()
            return imageUri.path ?: throw Exception("Uri invalid")
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to uploadImage local source")
        }
    }

    override suspend fun downloadImage(path: String): ByteArray {
        try {
            val imageReference = firebaseStorage.reference.child(path)
            val bytes = imageReference.getBytes(MAX_DOWNLOAD_BYTES).await()
            return bytes
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to downloadImage local source")
        }
    }
}