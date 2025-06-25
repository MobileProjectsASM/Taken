package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Session

interface SessionLocalSource {
    suspend fun fetchSession(): Session?
    suspend fun saveSession(session: Session)
    suspend fun closeSession()
}