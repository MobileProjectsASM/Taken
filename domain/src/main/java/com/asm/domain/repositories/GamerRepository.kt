package com.asm.domain.repositories

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface GamerRepository {
    suspend fun registerGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String = ""
    ): Result<String, GeneralError>

    suspend fun updateGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String = ""
    ): Result<String, GeneralError>

    suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralError>
    suspend fun updateGamerImage(gamerId: String, imageUrl: String): Result<Unit, GeneralError>
    suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GeneralError>
}