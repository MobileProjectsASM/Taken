package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.CommonFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GamerExistsUC @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val logger: Logger
): UseCaseSync<Result<Boolean, CommonFailure>, String>() {

    companion object {
        const val TAG = "gamer-exists-use-case"
    }

    override suspend fun run(params: String): Result<Boolean, CommonFailure> {
        return try {
            gamerRepository.verifyGamerExists(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }
}