package com.asm.domain.repositories

import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.utils.Completed

interface GameRepository {
    suspend fun saveGamerGames(games: List<Game>, gamerId: String): Result<Completed, GeneralFailure>
}