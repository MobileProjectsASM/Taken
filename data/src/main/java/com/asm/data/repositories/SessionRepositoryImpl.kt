package com.asm.data.repositories

import com.asm.data.sources.local.interfaces.SessionLocalSource
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralFailure
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

    override suspend fun isThereSessionActive(): Result<Session?, GeneralFailure> {
        return try {
            val session = sessionLocalSource.fetchSession()
            Result.Successful(session)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun saveSession(session: Session): Result<Unit, GeneralFailure> {
        return try {
            sessionLocalSource.saveSession(session)
            Result.Successful(Unit)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun closeSession(): Result<Unit, GeneralFailure> {
        return try {
            sessionLocalSource.closeSession()
            Result.Successful(Unit)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}