package com.asm.domain.repositories

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.utils.Completed

interface GamerRepository {
    suspend fun registerGamer(gamer: Gamer): Result<Completed, GeneralFailure>
    suspend fun checkIfGamerExists(gamerId: String): Result<Boolean, GeneralFailure>
    suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralFailure>
}