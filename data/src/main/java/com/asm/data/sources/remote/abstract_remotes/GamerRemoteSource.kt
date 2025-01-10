package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Gamer

interface GamerRemoteSource {
    suspend fun saveGamer(gamer: Gamer)
    suspend fun checkGamerExists(gamerId: String): Boolean
}