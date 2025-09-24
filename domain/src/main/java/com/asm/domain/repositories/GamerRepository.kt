package com.asm.domain.repositories

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GamerError

interface GamerRepository {
    suspend fun registerGamer(userId: String, gamerAlias: String, gamerAge: Int, gamerCountry: String): Result<String, GamerError>
    suspend fun getGamerById(gamerId: String): Result<Gamer?, GamerError>
    suspend fun updateGamerImage(gamerId: String, imageUrl: String): Result<Unit, GamerError>
    suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GamerError>
}