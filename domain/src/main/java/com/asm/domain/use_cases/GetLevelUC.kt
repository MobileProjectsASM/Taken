package com.asm.domain.use_cases

import com.asm.domain.entities.Level
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetLevelUC @Inject constructor(
    private val logger: Logger,
    private val levelRepository: LevelRepository
) : UseCaseSync<Result<Level, GeneralFailure>, Int>() {
    override suspend fun run(params: Int): Result<Level, GeneralFailure> {
        /*return try {
            levelRepository.getLevelByOrder(params)
        } catch (exception: Exception) {
            logger.logE { exception }
            Error.UnknownError.toFailure()
        }*/
        return Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
    }
}