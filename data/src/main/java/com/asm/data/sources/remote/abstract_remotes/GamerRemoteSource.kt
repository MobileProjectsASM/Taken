package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GamerError

interface GamerRemoteSource {
    suspend fun getGamerById(gamerId: String): Result<Gamer, GamerError>
    suspend fun saveGamer(userId: String, gamerAlias: String, gamerAge: Int, gamerCountry: String): Result<String, GamerError>
    suspend fun checkGamerExists(gamerId: String): Result<Boolean, GamerError>
    suspend fun updateGamerImage(gamerId: String, gamerImage: String): Result<Unit, GamerError>
}