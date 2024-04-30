package com.asm.domain.repositories

import com.asm.domain.entities.Game
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Either

interface GamerRepository {
    suspend fun registerGamer(gamer: Gamer): Either<Failure, Completed>
    suspend fun checkIfGamerExists(gamerId: String): Either<Failure, Boolean>
    suspend fun getGamerById(gamerId: String): Either<Failure, Gamer>
}