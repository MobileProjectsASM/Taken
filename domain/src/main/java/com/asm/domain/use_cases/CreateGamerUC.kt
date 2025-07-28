package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Level
import com.asm.domain.entities.LevelInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import java.util.UUID
import javax.inject.Inject

class CreateGamerUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val multimediaRepository: MultimediaRepository,
    private val logger: Logger
) : UseCaseSync<Result<String, GeneralFailure>, CreateGamerUC.GamerParams>() {

    companion object {
        const val PROFILE_IMAGE = "pi"
        const val TAG = "CreateGamerUc"
    }

    sealed class ProfileImage {
        data class InfoImage(
            val mimeType: String,
            val byteArray: ByteArray
        ): ProfileImage() {
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
        ): ProfileImage()
    }

    data class GamerParams(
        val gamerId: String,
        val nickName: String,
        val age: Int,
        val country: String,
        val image: ProfileImage?
    )

    override suspend fun run(params: GamerParams): Result<String, GeneralFailure> {
        return try {
            val resultCreateGamer = gamerRepository.registerGamer(
                userId = params.gamerId,
                gamerAlias = params.nickName,
                gamerAge = params.age,
                gamerCountry = params.country,
            )
            val gamerIdCreated = when (resultCreateGamer) {
                is Result.Successful<String> -> resultCreateGamer.data
                is Result.Unsuccessful<GeneralFailure> -> return resultCreateGamer
            }
            val imageUrl = when (params.image) {
                is ProfileImage.InfoImage -> {
                    val extension = params.image.mimeType.split("/").let { it[it.size - 1] }
                    val uploadImageResult = multimediaRepository.uploadUserImage(
                        userId = params.gamerId,
                        profileImageName = "${PROFILE_IMAGE}_${params.gamerId}.$extension",
                        byteArray = params.image.byteArray
                    )
                    if (uploadImageResult is Result.Unsuccessful) return uploadImageResult
                    uploadImageResult.asSuccessful().data
                }
                is ProfileImage.UrlImage -> params.image.urlImage
                null -> {
                    val defaultImageResult = multimediaRepository.getDefaultUserImage()
                    if (defaultImageResult is Result.Unsuccessful) return defaultImageResult
                    defaultImageResult.asSuccessful().data
                }
            }
            val updateImageResult = gamerRepository.updateGamerImage(gamerIdCreated, imageUrl)
            when (updateImageResult) {
                is Result.Successful<Unit> -> Result.Successful(gamerIdCreated)
                is Result.Unsuccessful<GeneralFailure> -> updateImageResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    /*override suspend fun run(params: GamerParams): Result<Unit, RegisterFailure> {
        return try {
            val resultGamerExists = gamerRepository.checkIfGamerExists(params.gamerId)
            if (resultGamerExists is Result.Unsuccessful) return Result.Unsuccessful(resultGamerExists.failure.toRegisterFailure())
            val gamerExists = resultGamerExists.asSuccessful().data
            if (gamerExists) return Result.Unsuccessful(RegisterFailure.GamerExists)
            val resultImage = if (params.image == null) {
                multimediaRepository.getDefaultUserImage()
            } else {
                multimediaRepository.uploadUserImage(
                    params.gamerId,
                    "${PROFILE_IMAGE}_${params.gamerId}.${params.image.formatImage}",
                    params.image.base64
                )
            }
            if (resultImage is Result.Unsuccessful) return Result.Unsuccessful(resultImage.failure.toRegisterFailure())
            val gamer = resultImage.asSuccessful().data.run {
                Gamer(params.gamerId, params.nickName, params.age, params.country, this)
            }
            val resultRegisterGamer = gamerRepository.registerGamer(gamer)
            if (resultRegisterGamer is Result.Unsuccessful) return Result.Unsuccessful(resultRegisterGamer.failure.toRegisterFailure())
            val levelsResult = levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2))
            if (levelsResult is Result.Unsuccessful) return Result.Unsuccessful(levelsResult.failure.toRegisterFailure())
            val initLevels = levelsResult.asSuccessful().data
            val initialGames = createInitGames(initLevels)
            val resultSaveGamerGames = gameRepository.saveGamerGames(initialGames, params.gamerId)
            if (resultSaveGamerGames is Result.Unsuccessful) return Result.Unsuccessful(resultSaveGamerGames.failure.toRegisterFailure())
            Result.Successful(Unit)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(RegisterFailure.General(GeneralFailure.Unknown))
        }
    }*/
}