package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CloseSessionUC @Inject constructor(
    private val logger: Logger,
    private val sessionRepository: SessionRepository
): UseCaseSync<Result<Unit, GeneralError>, suspend () -> Result<Unit, GeneralError>>() {

    companion object {
        const val TAG = "CloseSessionUC"
    }

    override suspend fun run(params: suspend () -> Result<Unit, GeneralError>): Result<Unit, GeneralError> {
        val signOut: suspend () -> Result<Unit, GeneralError> = params
        return try {
            when (val signOutResult = signOut()) {
                is Result.Successful<Unit> -> sessionRepository.closeSession()
                is Result.Unsuccessful<GeneralError> -> signOutResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}