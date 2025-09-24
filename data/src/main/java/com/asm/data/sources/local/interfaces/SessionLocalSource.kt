package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.SessionError

interface SessionLocalSource {
    suspend fun fetchSession(): Result<Session, SessionError>
    suspend fun saveSession(session: Session): Result<Unit, SessionError>
    suspend fun closeSession(): Result<Unit, SessionError>
}