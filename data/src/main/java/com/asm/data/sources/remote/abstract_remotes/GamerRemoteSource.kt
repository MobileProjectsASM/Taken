package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface GamerRemoteSource {
    suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralError>
    suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String = ""
    ): Result<String, GeneralError>

    suspend fun checkGamerExists(gamerId: String): Result<Boolean, GeneralError>
    suspend fun updateGamerImage(gamerId: String, gamerImage: String): Result<Unit, GeneralError>
}