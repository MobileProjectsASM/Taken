package com.asm.domain.entities

sealed class GameStatus {
    data object Lock: GameStatus()
    data object New: GameStatus()
    data class Process(
        val timePlaying: Int,
        val movesMade: Int
    ): GameStatus()
    data class Win(
        val timePlaying: Int,
        val movesMade: Int,
        val score: Double
    ): GameStatus()
}