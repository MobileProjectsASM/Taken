package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Level
import com.asm.domain.entities.LevelInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.errors.RegisterFailure
import com.asm.domain.errors.toRegisterFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import java.util.UUID
import javax.inject.Inject

class CreateGamerUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val levelRepository: LevelRepository,
    private val gameRepository: GameRepository,
    private val multimediaRepository: MultimediaRepository,
    private val logger: Logger
) : UseCaseSync<Result<Unit, RegisterFailure>, CreateGamerUC.GamerParams>() {

    companion object {
        const val PROFILE_IMAGE = "pi"
        const val TAG = "CreateGamerUc"
    }

    data class InfoImage(
        val formatImage: String,
        val base64: String
    )

    data class GamerParams(
        val gamerId: String,
        val nickName: String,
        val age: Int,
        val country: String,
        val image: InfoImage?
    )

    private fun createInitGames(levels: List<Level>): List<Game> {
        val initGames = mutableListOf<Game>()
        for (level in levels) {
            val levelInfo = LevelInfo(
                levelId = level.levelId,
                levelName = level.levelName,
                levelImage = level.levelImage
            )
            val gameId = UUID.randomUUID().toString()
            val game = Game(
                gameId = gameId,
                levelInfo = levelInfo,
                gameStatus = if (level.orderCriteria == 1) GameStatus.New else GameStatus.Lock
            )
            initGames.add(game)
        }
        return initGames
    }

    override suspend fun run(params: GamerParams): Result<Unit, RegisterFailure> {
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
    }
}