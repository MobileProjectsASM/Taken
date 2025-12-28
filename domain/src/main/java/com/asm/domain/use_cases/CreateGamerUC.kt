package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
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
        const val URL_IMAGE_PREFIX = "http"
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
            val uri = params.imageURI
            val imageUrl = when {
                uri == null -> {
                    when (val defaultImageResult = multimediaRepository.getDefaultUserImage()) {
                        is Result.Successful<String?> -> {
                            if (defaultImageResult.data == null) {
                                logger.logE(TAG, "Not found default image")
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

                    val imageName = getImageName(params.gamerId, metaDataImage.mimeType)
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
                    if (params.imageURI != null && !params.imageURI.startsWith(URL_IMAGE_PREFIX)) {
                        val imageName = imageUrl.split("/").let { it[it.size - 1] }
                        val resultDeleteImage = multimediaRepository.deleteUserImage(imageName)
                        when (resultDeleteImage) {
                            is Result.Successful<Unit> -> resultCreateGamer
                            is Result.Unsuccessful<GeneralError> -> resultDeleteImage
                        }
                    } else resultCreateGamer
                }
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private fun getImageName(gamerId: String, mimeType: String): String {
        val extension = mimeType.split("/").let { it[it.size - 1] }
        return "${PROFILE_IMAGE}_$gamerId.$extension"
    }
}