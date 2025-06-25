package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GamerFailure
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.errors.toGamerFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetGamerUC @Inject constructor(
    private val logger: Logger,
    private val gamerRepository: GamerRepository
): UseCaseSync<Result<Gamer, GamerFailure>, String>() {

    companion object {
        const val TAG = "GetGamerUC"
    }

    override suspend fun run(params: String): Result<Gamer, GamerFailure> {
        return try {
            val gamerResult = gamerRepository.getGamerById(params)
            if (gamerResult is Result.Unsuccessful) return Result.Unsuccessful(gamerResult.failure.toGamerFailure())
            val data = gamerResult.asSuccessful().data
            when {
                data == null -> Result.Unsuccessful(GamerFailure.GamerNotExists)
                else -> Result.Successful(data)
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GamerFailure.General(GeneralFailure.Unknown))
        }
    }
}