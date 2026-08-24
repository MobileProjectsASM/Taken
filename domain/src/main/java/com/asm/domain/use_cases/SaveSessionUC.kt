package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.CommonFailure
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SaveSessionUC @Inject constructor(
    private val logger: Logger,
    private val sessionRepository: SessionRepository
): UseCaseSync<Result<Unit, CommonFailure>, Session>() {

    companion object {
        const val TAG = "save-session-use-case"
    }

    override suspend fun run(params: Session): Result<Unit, CommonFailure> {
        return try {
            sessionRepository.saveSession(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }
}