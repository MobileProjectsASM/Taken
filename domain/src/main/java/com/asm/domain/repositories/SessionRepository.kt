package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError

interface SessionRepository {
    suspend fun isThereSessionActive(): Result<Session?, GeneralError>
    suspend fun saveSession(session: Session): Result<Unit, Failure>
    suspend fun closeSession(): Result<Unit, GeneralError>
}