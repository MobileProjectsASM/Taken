package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSyncWithoutParams
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetSessionUC @Inject constructor(
    private val logger: Logger,
    private val repository: SessionRepository
): UseCaseSyncWithoutParams<Result<Session?, GeneralError>>() {
    companion object {
        const val TAG = "GetSessionUC"
    }

    override suspend fun run(): Result<Session?, GeneralError> {
        return try {
            repository.isThereSessionActive()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}