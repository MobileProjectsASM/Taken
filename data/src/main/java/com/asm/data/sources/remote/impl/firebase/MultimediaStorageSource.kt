package com.asm.data.sources.remote.impl.firebase

import android.content.Context
import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MultimediaStorageSource @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val context: Context
): MultimediaRemoteSource {

    companion object {
        const val TAG = "MULTIMEDIA_STORAGE_SOURCE"
    }

    override suspend fun uploadResource(path: String, byteArray: ByteArray): Result<String, GeneralError> {
        return try {
            val imageReference = firebaseStorage.reference.child(path)
            imageReference.putBytes(byteArray).await()
            val imageUri = imageReference.downloadUrl.await()
            imageUri?.toString()?.let {
                Result.Successful(it)
            } ?: GeneralError.ServerError().toUnsuccessful().also {
                Log.e(TAG, "path is null")
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun getUrlResource(path: String): Result<String?, GeneralError> {
        return try {
            val resourceReference = firebaseStorage.reference.child(path)
            resourceReference.downloadUrl.await()?.toString()?.let {
                Result.Successful(it)
            } ?: Result.Successful(null)
        } catch(exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun deleteResource(path: String): Result<Unit, GeneralError> {
        return try {
            val resourceReference = firebaseStorage.reference.child(path)
            resourceReference.delete().await()
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}