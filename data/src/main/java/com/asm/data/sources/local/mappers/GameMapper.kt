package com.asm.data.sources.local.mappers

import javax.inject.Inject

class GameMapper @Inject constructor() {
    /*fun getGameRoom(game: Game, gamerId: String): GameRoom = when (val gameStatus = game.gameStatus) {
        GameStatus.Lock -> GameRoom(
            game.gameId,
            gamerId,
            game.levelInfo.levelId,
            "lock"
        )
        GameStatus.New -> GameRoom(
            game.gameId,
            gamerId,
            game.levelInfo.levelId,
            "new"
        )
        is GameStatus.Process -> GameRoom(
            game.gameId,
            gamerId,
            game.levelInfo.levelId,
            "process",
            gameStatus.movesMade,
            gameStatus.timePlaying
        )
        is GameStatus.Win -> GameRoom(
            game.gameId,
            gamerId,
            game.levelInfo.levelId,
            "win",
            gameStatus.movesMade,
            gameStatus.timePlaying,
            gameStatus.score
        )
    }*/
}