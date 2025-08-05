package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Gamer

interface GamerRemoteSource {
    suspend fun getGamerById(gamerId: String): Gamer
    suspend fun saveGamer(userId: String, gamerAlias: String, gamerAge: Int, gamerCountry: String): String
    suspend fun checkGamerExists(gamerId: String): Boolean
    suspend fun updateGamerImage(gamerId: String, gamerImage: String)
}