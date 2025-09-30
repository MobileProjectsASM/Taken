package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GamerExistsUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val logger: Logger
): UseCaseSync<Result<Boolean, GeneralError>, String>() {

    companion object {
        const val TAG = "GamerExistsUC"
    }

    override suspend fun run(params: String): Result<Boolean, GeneralError> {
        return try {
            gamerRepository.verifyGamerExists(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}