package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GameFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight

class GetGameInProcessUC(
    private val logger: Logger,
    private val gameRepository: GameRepository
) : UseCaseSync<Game, String>() {
    override suspend fun run(params: String): Either<Failure, Game> {
        try {
            val result = gameRepository.getGameInProcess(params)
            if (result.isLeft) return result as Either.Left
            val value = (result as Either.Right).r ?: return GameFailure.ThereIsNotGameInProcess.toLeft()
            return value.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }
}