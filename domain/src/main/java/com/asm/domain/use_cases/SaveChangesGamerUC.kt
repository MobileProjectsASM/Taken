package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.getImageExtension
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.CreateGamerUC.Companion.PROFILE_IMAGE
import com.asm.domain.use_cases.CreateGamerUC.Companion.URL_IMAGE_PREFIX
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SaveChangesGamerUC @Inject constructor(
    private val logger: Logger,
    private val multimediaRepository: MultimediaRepository,
    private val gamerRepository: GamerRepository
): UseCaseSync<Result<String, GeneralError>, SaveChangesGamerUC.GamerParams>() {

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
            //Update image
            val uri = params.imageURI
            val imageUrl = when {
                uri == null -> {
                    when (val defaultImageResult = multimediaRepository.getDefaultUserImage()) {
                        is Result.Successful<String?> -> {
                            if (defaultImageResult.data == null) {
                                logger.logE(CreateGamerUC.TAG, "Not found default image")
                                return GeneralError.Unknown.toUnsuccessful()
                            }
                            defaultImageResult.data
                        }

                        is Result.Unsuccessful<GeneralError> -> {
                            return defaultImageResult
                        }
                    }
                }

                uri.startsWith(URL_IMAGE_PREFIX) -> uri
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

            gamerRepository.updateGamer(
                userId = params.gamerId,
                gamerAlias = params.nickName,
                gamerAge = params.age,
                gamerCountry = params.country,
                gamerCountryFlag = params.countryFlag,
                gamerImage = imageUrl
            )
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralError.Unknown)
        }
    }
}