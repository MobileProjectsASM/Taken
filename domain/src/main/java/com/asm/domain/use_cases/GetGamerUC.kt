package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight

class GetGamerUC(
    private val logger: Logger,
    private val gamerRepository: GamerRepository
): UseCaseSync<Gamer, String>() {
    override suspend fun run(params: String): Either<Failure, Gamer> {
        try {
            val result = gamerRepository.getGamerById(params)
            if (result.isLeft) return result as Either.Left
            val value = (result as Either.Right).r
            return value.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }
}