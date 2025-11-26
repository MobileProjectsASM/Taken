package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface GameRemoteSource {
    //suspend fun insertGames(games: List<Game>, gamerId: String)
    suspend fun getGamesByGamerId(gamerId: String): Result<List<Game>, GeneralError>
}