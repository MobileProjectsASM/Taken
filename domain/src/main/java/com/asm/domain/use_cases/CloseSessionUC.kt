package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CloseSessionUC @Inject constructor(
    private val logger: Logger,
    private val sessionRepository: SessionRepository
): UseCaseSync<Result<Unit, GeneralFailure>, suspend () -> Result<Unit, GeneralFailure>>() {

    companion object {
        const val TAG = "CloseSessionUC"
    }

    override suspend fun run(params: suspend () -> Result<Unit, GeneralFailure>): Result<Unit, GeneralFailure> {
        val signOut: suspend () -> Result<Unit, GeneralFailure> = params
        return try {
            when (val signOutResult = signOut()) {
                is Result.Successful<Unit> -> sessionRepository.closeSession()
                is Result.Unsuccessful<GeneralFailure> -> signOutResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}