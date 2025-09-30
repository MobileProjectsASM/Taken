package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SaveSessionUC @Inject constructor(
    private val logger: Logger,
    private val sessionRepository: SessionRepository
): UseCaseSync<Result<Unit, GeneralError>, Session>() {

    companion object {
        const val TAG = "SaveSessionUC"
    }

    override suspend fun run(params: Session): Result<Unit, GeneralError> {
        return try {
            sessionRepository.saveSession(params)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}