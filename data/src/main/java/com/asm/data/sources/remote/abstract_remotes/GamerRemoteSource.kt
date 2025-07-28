package com.asm.data.sources.remote.abstract_remotes

interface GamerRemoteSource {
    suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): String
    suspend fun checkGamerExists(gamerId: String): Boolean
    suspend fun updateGamerImage(gamerId: String, gamerImage: String)
}