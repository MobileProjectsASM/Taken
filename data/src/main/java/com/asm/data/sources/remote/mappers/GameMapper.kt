package com.asm.data.sources.remote.mappers

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.data.sources.remote.model.GameFireStore as GameFireStore
import javax.inject.Inject

class GameMapper @Inject constructor() {

    companion object {
        const val TIME_PLAYING = "time_playing"
        const val MOVES_MADE = "moves_made"
        const val SCORE = "score"
    }

    fun getGameFireStore(game: Game) : GameFireStore = when (val gameStatus = game.gameStatus) {
        GameStatus.New -> GameFireStore(
            game.gameId,
            game.levelInfo.levelId,
            mapOf("new" to null)
        )
        GameStatus.Lock -> GameFireStore(
            game.gameId,
            game.levelInfo.levelId,
            mapOf("lock" to null)
        )
        is GameStatus.Process -> GameFireStore(
            game.gameId,
            game.levelInfo.levelId,
            mapOf("process" to (mapOf(
                TIME_PLAYING to gameStatus.timePlaying,
                MOVES_MADE to gameStatus.movesMade
            )))
        )
        is GameStatus.Win -> GameFireStore(
            game.gameId,
            game.levelInfo.levelId,
            mapOf("process" to (mapOf(
                TIME_PLAYING to gameStatus.timePlaying,
                MOVES_MADE to gameStatus.movesMade,
                SCORE to gameStatus.score
            )))
        )
    }
}