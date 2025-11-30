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
    }

    sealed class ProfileImage {
        data class InfoImage(
            val mimeType: String,
            val byteArray: ByteArray
        ) : ProfileImage() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as InfoImage

                if (mimeType != other.mimeType) return false
                if (!byteArray.contentEquals(other.byteArray)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = mimeType.hashCode()
                result = 31 * result + byteArray.contentHashCode()
                return result
            }
        }

        data class UrlImage(
            val urlImage: String
        ) : ProfileImage()
    }

    data class GamerParams(
        val gamerId: String,
        val nickName: String,
        val age: Int,
        val country: String,
        val image: ProfileImage?
    )

    override suspend fun run(params: GamerParams): Result<String, GeneralError> {
        return try {
            //Update image
            val imageUrl = when (params.image) {
                is ProfileImage.InfoImage -> {
                    val imageName = getImageName(params.gamerId, params.image)
                    val uploadImageResult = multimediaRepository.uploadUserImage(
                        userId = params.gamerId,
                        profileImageName = imageName,
                        byteArray = params.image.byteArray
                    )
                    when (uploadImageResult) {
                        is Result.Successful<String> -> uploadImageResult.asSuccessful().data
                        is Result.Unsuccessful<GeneralError> -> return uploadImageResult
                    }
                }

                is ProfileImage.UrlImage -> params.image.urlImage
                null -> {
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
            }

            //Update registerGamer
            val resultCreateGamer = gamerRepository.registerGamer(
                userId = params.gamerId,
                gamerAlias = params.nickName,
                gamerAge = params.age,
                gamerCountry = params.country,
                gamerImage = imageUrl
            )
            when (resultCreateGamer) {
                is Result.Successful<String> -> Result.Successful(resultCreateGamer.data)
                is Result.Unsuccessful<GeneralError> -> {
                    if (params.image is ProfileImage.InfoImage) {
                        val imageName = getImageName(params.gamerId, params.image)
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

    private fun getImageName(gamerId: String, infoImage: ProfileImage.InfoImage): String {
        val extension = infoImage.mimeType.split("/").let { it[it.size - 1] }
        return "${PROFILE_IMAGE}_$gamerId.$extension"
    }
}