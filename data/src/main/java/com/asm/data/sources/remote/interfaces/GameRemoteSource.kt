package com.asm.data.sources.remote.interfaces

import com.asm.domain.entities.Game

interface GameRemoteSource {
    suspend fun insertGames(games: List<Game>)
}