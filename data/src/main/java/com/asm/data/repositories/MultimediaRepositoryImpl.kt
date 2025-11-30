package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class MultimediaRepositoryImpl @Inject constructor(
    private val multimediaRemoteSource: MultimediaRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
) : MultimediaRepository {

    companion object {
        const val PROFILE_IMAGES_PATH = "images/profile"
        const val DEFAULT_PROFILE_IMAGE = "default_profile_image.png"
        const val TAG = "MultimediaRepositoryImpl"
    }

    override suspend fun uploadUserImage(
        userId: String,
        profileImageName: String,
        byteArray: ByteArray
    ): Result<String, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralError.NetworkError)
            val imagePath = "$PROFILE_IMAGES_PATH/$profileImageName"
            multimediaRemoteSource.uploadResource(imagePath, byteArray)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun deleteUserImage(imageName: String): Result<Unit, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralError.NetworkError)
            val imagePath = "$PROFILE_IMAGES_PATH/$imageName"
            multimediaRemoteSource.deleteResource(imagePath)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun getDefaultUserImage(): Result<String?, GeneralError> {
        return try {
            val fullPath = "$PROFILE_IMAGES_PATH/$DEFAULT_PROFILE_IMAGE"
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralError.NetworkError)
            multimediaRemoteSource.getUrlResource(fullPath)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}