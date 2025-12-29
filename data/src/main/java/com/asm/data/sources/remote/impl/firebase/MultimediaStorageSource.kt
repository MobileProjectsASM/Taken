package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MultimediaStorageSource @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : MultimediaRemoteSource {

    companion object {
        const val TAG = "MULTIMEDIA_STORAGE_SOURCE"
    }

    override suspend fun uploadResource(
        path: String,
        byteArray: ByteArray
    ): Result<String, GeneralError> {
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
            resourceReference.downloadUrl.await().toString().let {
                Result.Successful(it)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            if (exception is StorageException && exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND)
                Result.Successful(null)
            else GeneralError.Unknown.toUnsuccessful()
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

    override suspend fun deleteResourceByUrl(url: String): Result<Boolean, GeneralError> {
        return try {
            val resourceReference = firebaseStorage.getReferenceFromUrl(url)
            resourceReference.delete().await()
            Result.Successful(true)
        } catch (e: Exception) {
            Log.e(TAG, "error to delete resource by url", e)
            if (e is IllegalArgumentException) Result.Successful(false)
            else GeneralError.Unknown.toUnsuccessful()
        }
    }
}