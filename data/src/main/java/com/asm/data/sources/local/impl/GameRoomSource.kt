package com.asm.data.sources.local.impl

import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.data.sources.local.mappers.GameMapper
import com.asm.domain.entities.Game
import javax.inject.Inject

class GameRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val gameMapper: GameMapper
): GameLocalSource {

    companion object {
        const val TAG = "GameRoomSource"
    }

    override suspend fun saveGamesByGamerId(games: List<Game>, gamerId: String) {
        try {
            val gamesRoom = games.map { gameMapper.getGameRoom(it, gamerId) }
            takenDB.getGameDao().insertAll(gamesRoom)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveGameByGamerId in local source")
        }
    }

}