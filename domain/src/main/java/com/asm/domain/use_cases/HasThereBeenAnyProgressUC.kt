package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.repositories.GameRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class HasThereBeenAnyProgressUC @Inject constructor(
    private val gameRepository: GameRepository,
    private val logger: Logger
) : UseCaseSync<Result<Boolean, GeneralError>, String>() {

    companion object {
        const val TAG = "HasThereBeenAnyProgressUC"
    }

    override suspend fun run(params: String): Result<Boolean, GeneralError> {
        return try {
            val games = when (val result = gameRepository.getGamesByGamer(params)) {
                is Result.Successful<List<Game>> -> result.data
                is Result.Unsuccessful<GeneralError> -> return result
            }
            val isThereProgress = games.find {
                it.status is GameStatus.Win || it.status is GameStatus.Process
            }?.let { true } ?: false
            Result.Successful(isThereProgress)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralError.Unknown)
        }
    }
}