package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Game

interface GameLocalSource {
    suspend fun saveGamesByGamerId(games: List<Game>, gamerId: String)
}