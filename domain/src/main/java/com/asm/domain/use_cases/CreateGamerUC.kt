package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Level
import com.asm.domain.entities.LevelInfo
import com.asm.domain.errors.Failure
import com.asm.domain.errors.RegisterFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import javax.inject.Inject

class CreateGamerUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val levelRepository: LevelRepository,
    private val gameRepository: GameRepository,
    private val multimediaRepository: MultimediaRepository,
    private val logger: Logger
) : UseCaseSync<Completed, CreateGamerUC.GamerParams>() {

    companion object {
        const val PROFILE_IMAGE_NAME = "profile_image"
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

    override suspend fun run(params: GamerParams): Either<Failure, Completed> {
        try {
            val resultImage = if (params.image == null) {
                multimediaRepository.getDefaultUserImage()
            } else {
                multimediaRepository.uploadUserImage(
                    params.gamerId,
                    "$PROFILE_IMAGE_NAME.${params.image.formatImage}",
                    params.image.base64
                )
            }
            if (resultImage.isLeft) return resultImage as Either.Left
            val imagePath = (resultImage as Either.Right).r
            val resultRegisterGamer = gamerRepository.registerGamer(
                Gamer(params.gamerId, params.nickName, params.age, params.country, imagePath)
            )
            if (resultRegisterGamer.isLeft) return resultRegisterGamer as Either.Left
            val levelsResult = levelRepository.getRangeLevels(finalRange = 2)
            if (levelsResult.isLeft) return levelsResult as Either.Left
            val initLevels = (levelsResult as Either.Right).r
            val initialGames = createInitGames(initLevels)
            val resultRegisterGames = gameRepository.saveGames(initialGames)
            if (resultRegisterGamer.isLeft) return resultRegisterGames as Either.Left
            return resultRegisterGamer
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }

    private fun createInitGames(levels: List<Level>): List<Game> {
        val initGames = mutableListOf<Game>()
        for (level in levels) {
            val levelInfo = LevelInfo(
                levelOrder = level.levelOrder,
                levelName = level.levelName,
                levelImage = level.levelImage
            )
            val game = Game(
                levelInfo = levelInfo,
                gameStatus = if (level.levelOrder == 1) GameStatus.New else GameStatus.Lock
            )
            initGames.add(game)
        }
        return initGames
    }
}