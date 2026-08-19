package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError

interface SessionLocalSource {
    suspend fun fetchSession(): Result<Session?, GeneralError>
    suspend fun saveSession(session: Session): Result<Unit, Failure>
    suspend fun closeSession(): Result<Unit, GeneralError>
}