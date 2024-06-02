package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Error
import com.asm.domain.errors.GameError
import com.asm.domain.repositories.GameRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetMainGamesUC @Inject constructor(
    private val logger: Logger,
    private val gameRepository: GameRepository
) : UseCaseSync<List<Game>, String>() {
    override suspend fun run(params: String): Result<List<Game>> {
        return try {
            val result = gameRepository.getGamerGames(params)
            if (result.isFailure) return result
            val allGamesByUser = result.asSuccessful().data
            val thereIsGameInProcess = allGamesByUser.any { it.gameStatus is GameStatus.Process }
            if (thereIsGameInProcess) return GameError.ThereIsGameInProcess.toFailure()
            val thereAreMoreThanOneNew = allGamesByUser.filter { it.gameStatus is GameStatus.New }.size > 1
            if (thereAreMoreThanOneNew) return GameError.MoreThanOneNewGame.toFailure()
            val thereAreMoreThanOneLock = allGamesByUser.filter { it.gameStatus is GameStatus.Lock }.size > 1
            if (thereAreMoreThanOneLock) return GameError.MoreThanOneLockGame.toFailure()
            val gamesGroupByLevelOrder = allGamesByUser.groupBy { it.levelInfo.levelOrder }

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
            val mainGamesSorted = mainGames.values.sortedBy { it.levelInfo.levelOrder}
            mainGamesSorted.toSuccessful()
        } catch (exception: Exception) {
            logger.logE { exception }
            Error.UnknownError.toFailure()
        }
    }
}