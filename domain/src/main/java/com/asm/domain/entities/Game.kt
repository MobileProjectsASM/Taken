package com.asm.domain.entities

data class Game(
    val gameId: String,
    val levelInfo: LevelInfo,
    val gameStatus: GameStatus,
)