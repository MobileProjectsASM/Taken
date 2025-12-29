package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.getImageExtension
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.SaveChangesGamerUC.Companion
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CreateGamerUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val multimediaRepository: MultimediaRepository,
    private val logger: Logger
) : UseCaseSync<Result<String, GeneralError>, CreateGamerUC.GamerParams>() {

    companion object {
        const val PROFILE_IMAGE = "pi"
        const val TAG = "CreateGamerUc"
        const val URL_PREFIX = "http"
    }

    data class GamerParams(
        val gamerId: String,
        val nickName: String,
        val age: Int,
        val country: String,
        val countryFlag: String?,
        val imageURI: String?
    )

    override suspend fun run(params: GamerParams): Result<String, GeneralError> {
        return try {
            //Update image
            val defaultImage =
                when (val defaultImageResult = multimediaRepository.getDefaultUserImage()) {
                    is Result.Successful<String?> -> {
                        if (defaultImageResult.data == null) {
                            logger.logE(SaveChangesGamerUC.TAG, "Not found default image")
                            return GeneralError.Unknown.toUnsuccessful()
                        }
                        defaultImageResult.data
                    }

                    is Result.Unsuccessful<GeneralError> -> return defaultImageResult
                }

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

            //Update registerGamer
            val resultCreateGamer = gamerRepository.registerGamer(
                userId = params.gamerId,
                gamerAlias = params.nickName,
                gamerAge = params.age,
                gamerCountry = params.country,
                gamerCountryFlag = params.countryFlag,
                gamerImage = imageUrl
            )
            when (resultCreateGamer) {
                is Result.Successful<String> -> Result.Successful(resultCreateGamer.data)
                is Result.Unsuccessful<GeneralError> -> {
                    if (imageUrl != defaultImage) {
                        val deleteImageResult = multimediaRepository.deleteResourceByUrl(imageUrl)
                        when (deleteImageResult) {
                            is Result.Successful<Boolean> -> resultCreateGamer
                            is Result.Unsuccessful<GeneralError> -> deleteImageResult
                        }
                    } else resultCreateGamer
                }
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

}