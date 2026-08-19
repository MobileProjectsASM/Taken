package com.asm.data.repositories

import com.asm.data.sources.local.interfaces.SessionLocalSource
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val sessionLocalSource: SessionLocalSource
): SessionRepository {

    companion object {
        const val TAG = "SessionRepositoryImpl"
    }

    override suspend fun isThereSessionActive(): Result<Session?, GeneralError> {
        return try {
            sessionLocalSource.fetchSession()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun saveSession(session: Session): Result<Unit, Failure> {
        return try {
            sessionLocalSource.saveSession(session).let { Result.Successful(Unit) }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(Failure.UnexpectedFailure)
        }
    }

    override suspend fun closeSession(): Result<Unit, GeneralError> {
        return try {
            sessionLocalSource.closeSession().let { Result.Successful(Unit) }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}