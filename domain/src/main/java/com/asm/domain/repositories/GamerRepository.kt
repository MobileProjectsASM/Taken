package com.asm.domain.repositories

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure

interface GamerRepository {
    suspend fun registerGamer(userId: String, gamerAlias: String, gamerAge: Int, gamerCountry: String): Result<String, GeneralFailure>
    suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralFailure>
    suspend fun updateGamerImage(gamerId: String, imageUrl: String): Result<Unit, GeneralFailure>
    suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GeneralFailure>
}