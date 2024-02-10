package com.asm.domain.repositories

import com.asm.domain.entities.Game
import com.asm.domain.errors.Failure
import com.asm.domain.utils.Either

interface GameRepository {
    suspend fun getGameInProcess(gamerId: String): Either<Failure, Game?>
    suspend fun getGamesByGamerId(gamerId: String): Either<Failure, List<Game>>
}