package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Game

interface GameRemoteSource {
    suspend fun insertGames(games: List<Game>, gamerId: String)
}