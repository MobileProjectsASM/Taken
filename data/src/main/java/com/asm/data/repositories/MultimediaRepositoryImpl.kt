package com.asm.data.repositories

import android.util.Log
import com.asm.data.sources.hardware.Connection
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.utils.Either
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight
import javax.inject.Inject

class MultimediaRepositoryImpl @Inject constructor(
    val multimediaLocalSource: MultimediaLocalSource,
    val multimediaRemoteSource: MultimediaRemoteSource,
    val connection: Connection
): MultimediaRepository {

    companion object {
        const val DEFAULT_PATH_USER_IMAGES = "images/profile"
        const val DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE = "default/images/"
        const val DEFAULT_IMAGE_NAME_PROFILE = "default_profile_image.jpg"
        const val TAG = "MultimediaRepositoryImpl"
    }
    override suspend fun uploadUserImage(userId: String, profileImageName: String, base64: String): Either<Failure, String> {
        if (!connection.thereIsInternetConnection()) return Failure.NetworkConnection.toLeft()
        val folderPath = "$userId/$DEFAULT_PATH_USER_IMAGES"
        try {
            multimediaRemoteSource.uploadImage(folderPath, profileImageName, base64)
            val imagePath = multimediaLocalSource.saveImage(folderPath, profileImageName, base64)
            return imagePath.toRight()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }

    override suspend fun getDefaultUserImage(): Either<Failure, String> {
        try {
            val fullPath = "$DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE/$DEFAULT_IMAGE_NAME_PROFILE"
            val existsImage = multimediaLocalSource.existsImage(fullPath)
            if (existsImage) return fullPath.toRight()
            if (!connection.thereIsInternetConnection()) return Failure.NetworkConnection.toLeft()
            val base64 = multimediaRemoteSource.downloadImage(fullPath)
            return multimediaLocalSource.saveImage(
                DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE,
                DEFAULT_IMAGE_NAME_PROFILE,
                base64
            ).toRight()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }
}