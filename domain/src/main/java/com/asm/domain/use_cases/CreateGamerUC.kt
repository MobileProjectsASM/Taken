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
    private val logger: Logger
) : UseCaseSync<Completed, Gamer>() {
    override suspend fun run(params: Gamer): Either<Failure, Completed> {
        try {
            val result = gamerRepository.checkIfGamerExists(params.gamerId)
            if (result.isLeft) return result as Either.Left
            val gamerExists = (result as Either.Right).r
            if (gamerExists) return Either.Left(RegisterFailure.GamerExists)
            val resultRegisterGamer = gamerRepository.registerGamer(params)
            if (resultRegisterGamer.isLeft) return resultRegisterGamer as Either.Left
            val levelsResult = levelRepository.getRangeLevels(finalRange = 2)
            if (levelsResult.isLeft) return levelsResult as Either.Left
            val levels = (levelsResult as Either.Right).r
            val initialGames = createInitGames(levels)
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