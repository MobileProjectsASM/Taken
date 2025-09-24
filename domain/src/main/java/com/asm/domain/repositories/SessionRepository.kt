package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.SessionError

interface SessionRepository {
    suspend fun isThereSessionActive(): Result<Session, SessionError>
    suspend fun saveSession(session: Session): Result<Unit, SessionError>
    suspend fun closeSession(): Result<Unit, SessionError>
}