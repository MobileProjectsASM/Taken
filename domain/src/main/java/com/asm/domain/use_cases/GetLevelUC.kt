package com.asm.domain.use_cases

import com.asm.domain.entities.Level
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight
import javax.inject.Inject

class GetLevelUC @Inject constructor(
    private val logger: Logger,
    private val levelRepository: LevelRepository
) : UseCaseSync<Level, Int>() {
    override suspend fun run(params: Int): Either<Failure, Level> {
        try {
            val result = levelRepository.getLevelByOrder(params)
            if (result.isLeft) return result as Either.Left
            val value = (result as Either.Right).r
            return value.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }
}