package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
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
        const val PROFILE_IMAGES_PATH = "images/profile"
        const val DEFAULT_PROFILE_IMAGE = "default_profile_image.png"
        const val TAG = "MultimediaRepositoryImpl"
    }
    override suspend fun uploadUserImage(userId: String, profileImageName: String, byteArray: ByteArray): Result<String, GeneralFailure> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.NetworkConnection)
            val imagePath = "$PROFILE_IMAGES_PATH/$profileImageName"
            val imageUrl = multimediaRemoteSource.uploadResource(imagePath, byteArray)
            Result.Successful(imageUrl)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun getDefaultUserImage(): Result<String, GeneralFailure> {
        return try {
            val fullPath = "$PROFILE_IMAGES_PATH/$DEFAULT_PROFILE_IMAGE"
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.NetworkConnection)
            val defaultImageUrl = multimediaRemoteSource.getUrlResource(fullPath)
            return Result.Successful(defaultImageUrl)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}