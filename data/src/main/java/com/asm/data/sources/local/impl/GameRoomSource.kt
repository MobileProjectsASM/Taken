package com.asm.data.sources.local.impl

import androidx.room.withTransaction
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.domain.entities.Game

class GameRoomSource(
    private val takenDB: TakenDB,
): GameLocalSource {
    override suspend fun saveGamesByGamerId(games: List<Game>, gamerId: String) {
        takenDB.withTransaction {

        }
    }
}