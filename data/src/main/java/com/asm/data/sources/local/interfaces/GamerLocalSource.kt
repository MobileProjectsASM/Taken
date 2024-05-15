package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Gamer

interface GamerLocalSource {
    suspend fun saveGamer(gamer: Gamer)
    suspend fun getGamer(gamerId: String): Gamer
    suspend fun checkGamerExists(gamerId: String): Boolean
}