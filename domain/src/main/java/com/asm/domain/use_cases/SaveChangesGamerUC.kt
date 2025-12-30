package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.getImageExtension
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.CreateGamerUC.Companion.PROFILE_IMAGE
import com.asm.domain.use_cases.CreateGamerUC.Companion.URL_PREFIX
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SaveChangesGamerUC @Inject constructor(
    private val logger: Logger,
    private val multimediaRepository: MultimediaRepository,
    private val gamerRepository: GamerRepository
) : UseCaseSync<Result<String, GeneralError>, SaveChangesGamerUC.GamerParams>() {

    data class GamerParams(
        val gamerId: String,
        val nickName: String,
        val age: Int,
        val country: String,
        val countryFlag: String?,
        val imageURI: String?
    )

    companion object {
        const val TAG = "Save Changes Gamer Use Case"
    }

    override suspend fun run(params: GamerParams): Result<String, GeneralError> {
        return try {
            //Get Default image
            val defaultImage =
                when (val defaultImageResult = multimediaRepository.getDefaultUserImage()) {
                    is Result.Successful<String?> -> {
                        if (defaultImageResult.data == null) {
                            logger.logE(TAG, "Not found default image")
                            return GeneralError.Unknown.toUnsuccessful()
                        }
                        defaultImageResult.data
                    }

                    is Result.Unsuccessful<GeneralError> -> return defaultImageResult
                }

            //Delete current image
            val gamerResult = gamerRepository.getGamerById(params.gamerId)
            val currentImage = when (gamerResult) {
                is Result.Successful<Gamer?> -> {
                    if (gamerResult.data == null) {
                        logger.logE(TAG, "Not found current gamer")
                        return GeneralError.Unknown.toUnsuccessful()
                    }
                    gamerResult.data.gamerImage
                }
                is Result.Unsuccessful<GeneralError> -> return gamerResult
            }

            if (currentImage != params.imageURI && currentImage != defaultImage) {
                val deleteResourceResult = multimediaRepository.deleteResourceByUrl(currentImage)
                if (deleteResourceResult is Result.Unsuccessful) return deleteResourceResult
            }

            //Update image
            val uri = params.imageURI
            val imageUrl = when {
                uri == null -> defaultImage

                uri.startsWith(URL_PREFIX) -> uri
                else -> {
                    val metaDataImageResult = multimediaRepository.getFileContent(uri)
                    if (metaDataImageResult is Result.Unsuccessful) return metaDataImageResult
                    val metaDataImage = metaDataImageResult.asSuccessful().data

                    val imageName =
                        "${PROFILE_IMAGE}_${params.gamerId}.${metaDataImage.mimeType.getImageExtension()}"
                    val uploadImageResult = multimediaRepository.uploadUserImage(
                        userId = params.gamerId,
                        profileImageName = imageName,
                        byteArray = metaDataImage.content
                    )
                    when (uploadImageResult) {
                        is Result.Successful<String> -> uploadImageResult.asSuccessful().data
                        is Result.Unsuccessful<GeneralError> -> return uploadImageResult
                    }
                }
            }

            //Update gamer
            val updateGamerResult = gamerRepository.updateGamer(
                userId = params.gamerId,
                gamerAlias = params.nickName,
                gamerAge = params.age,
                gamerCountry = params.country,
                gamerCountryFlag = params.countryFlag,
                gamerImage = imageUrl
            )

            when (updateGamerResult) {
                is Result.Successful<String> -> updateGamerResult
                is Result.Unsuccessful<GeneralError> -> {
                    if (imageUrl != defaultImage) {
                        val deleteImageResult = multimediaRepository.deleteResourceByUrl(imageUrl)
                        when (deleteImageResult) {
                            is Result.Successful<Boolean> -> updateGamerResult
                            is Result.Unsuccessful<GeneralError> -> deleteImageResult
                        }
                    } else updateGamerResult
                }
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralError.Unknown)
        }
    }
}