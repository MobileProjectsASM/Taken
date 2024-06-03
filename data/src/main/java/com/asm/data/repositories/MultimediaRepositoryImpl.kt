package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Error
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class MultimediaRepositoryImpl @Inject constructor(
    private val multimediaLocalSource: MultimediaLocalSource,
    private val multimediaRemoteSource: MultimediaRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
): MultimediaRepository {

    companion object {
        const val DEFAULT_PATH_USER_IMAGES = "images/profile"
        const val DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE = "default/images/"
        const val DEFAULT_IMAGE_NAME_PROFILE = "default_profile_image.jpg"
        const val TAG = "MultimediaRepositoryImpl"
    }
    override suspend fun uploadUserImage(userId: String, profileImageName: String, base64: String): Result<String> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Error.NetworkConnection.toFailure()
            val folderPath = "$userId/$DEFAULT_PATH_USER_IMAGES"
            multimediaRemoteSource.uploadImage(folderPath, profileImageName, base64)
            val imagePath = multimediaLocalSource.saveImage(folderPath, profileImageName, base64)
            imagePath.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Error.UnknownError.toFailure()
        }
    }

    override suspend fun getDefaultUserImage(): Result<String> {
        return try {
            val fullPath = "$DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE/$DEFAULT_IMAGE_NAME_PROFILE"
            val existsImage = multimediaLocalSource.existsImage(fullPath)
            if (existsImage) return fullPath.toSuccessful()
            if (!connectionSource.isNetworkAvailable()) return Error.NetworkConnection.toFailure()
            val base64 = multimediaRemoteSource.downloadImage(fullPath)
            return multimediaLocalSource.saveImage(
                DEFAULT_FOLDER_PATH_USER_IMAGE_PROFILE,
                DEFAULT_IMAGE_NAME_PROFILE,
                base64
            ).toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Error.UnknownError.toFailure()
        }
    }
}