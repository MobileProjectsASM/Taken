package com.asm.domain.repositories

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.utils.Completed

interface GamerRepository {
    suspend fun registerGamer(gamer: Gamer): Result<Completed>
    suspend fun checkIfGamerExists(gamerId: String): Result<Boolean>
    suspend fun getGamerById(gamerId: String): Result<Gamer>
}