package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetGamerUC @Inject constructor(
    private val logger: Logger,
    private val gamerRepository: GamerRepository
): UseCaseSync<Gamer, String>() {

    companion object {
        const val TAG = "GetGamerUC"
    }

    override suspend fun run(params: String): Result<Gamer> {
        return try {
            gamerRepository.getGamerById(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.UnknownFailure.toFailure()
        }
    }
}