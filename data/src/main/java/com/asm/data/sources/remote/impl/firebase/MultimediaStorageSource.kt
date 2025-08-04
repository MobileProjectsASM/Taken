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
    }

    override suspend fun uploadResource(path: String, byteArray: ByteArray): String {
        try {
            val imageReference = firebaseStorage.reference.child(path)
            imageReference.putBytes(byteArray).await()
            val imageUri = imageReference.downloadUrl.await()
            return imageUri.path ?: throw Exception("Resource url is null")
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to uploadImage local source")
        }
    }

    override suspend fun getUrlResource(path: String): String {
        try {
            val resourceReference = firebaseStorage.reference.child(path)
            val resourceUrl = resourceReference.downloadUrl.await().path
                ?: throw Exception("Resource url is null")
            return resourceUrl
        } catch(exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to get path image source")
        }
    }
}