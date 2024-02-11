package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GameFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight

class GetMainGamesUC(
    private val logger: Logger,
    private val gameRepository: GameRepository
) : UseCaseSync<List<Game>, String>() {
    override suspend fun run(params: String): Either<Failure, List<Game>> {
        try {
            val result = gameRepository.getGamesByGamerId(params)
            if (result.isLeft) return result as Either.Left
            val allGamesByUser = (result as Either.Right).r
            val thereIsGameInProcess = allGamesByUser.any { it.gameStatus is GameStatus.Process }
            if (thereIsGameInProcess) return GameFailure.ThereIsGameInProcess.toLeft()
            val thereAreMoreThanOneNew = allGamesByUser.filter { it.gameStatus is GameStatus.New }.size > 1
            if (thereAreMoreThanOneNew) return GameFailure.MoreThanOneNewGame.toLeft()
            val thereAreMoreThanOneLock = allGamesByUser.filter { it.gameStatus is GameStatus.Lock }.size > 1
            if (thereAreMoreThanOneLock) return GameFailure.MoreThanOneLockGame.toLeft()
            val gamesGroupByLevelOrder = allGamesByUser.groupBy { it.level.levelOrder }

            //get main games
            val mainGames = mutableMapOf<Int, Game>()
            for (entry in gamesGroupByLevelOrder.entries) {
                val allGamesByOrder = entry.value
                val gamesWin = allGamesByOrder.filter { it.gameStatus is GameStatus.Win }
                if (gamesWin.isNotEmpty()) {
                    val highestScoringGame =
                        gamesWin.maxBy { (it.gameStatus as GameStatus.Win).score }
                    mainGames[entry.key] = highestScoringGame
                    continue
                }
                val newGame = allGamesByOrder.firstOrNull { it.gameStatus is GameStatus.New }
                if (newGame != null) {
                    mainGames[entry.key] = newGame
                    continue
                }
                val lockGame = allGamesByOrder.firstOrNull { it.gameStatus is GameStatus.Lock }
                if (lockGame != null) {
                    mainGames[entry.key] = lockGame
                }
            }

            //Sort by order main games
            val mainGamesSorted = mainGames.values.sortedBy { it.level.levelOrder }
            return mainGamesSorted.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }
}