package com.asm.domain.repositories

import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface GameRepository {
    suspend fun getGamesByGamer(gamerId: String): Result<List<Game>, GeneralError>
}