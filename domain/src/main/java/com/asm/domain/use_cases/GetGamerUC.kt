package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetGamerUC @Inject constructor(
    private val logger: Logger,
    private val gamerRepository: GamerRepository
): UseCaseSync<Result<Gamer?, GeneralError>, String>() {

    companion object {
        const val TAG = "GetGamerUC"
    }

    override suspend fun run(params: String): Result<Gamer?, GeneralError> {
        return try {
            gamerRepository.getGamerById(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}