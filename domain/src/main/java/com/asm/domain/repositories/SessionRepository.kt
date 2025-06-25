package com.asm.domain.repositories

import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralFailure

interface SessionRepository {
    suspend fun isThereSessionActive(): Result<Session?, GeneralFailure>
}