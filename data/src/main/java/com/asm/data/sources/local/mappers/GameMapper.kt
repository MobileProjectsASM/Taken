package com.asm.data.sources.local.mappers

import com.asm.domain.entities.Game as GameDomain
import com.asm.data.sources.local.entities.Game

class GameMapper {
    fun getGame(gameDomain: GameDomain): Game = Game(
        gameDomain.
    )
}