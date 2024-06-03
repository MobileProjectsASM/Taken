package com.asm.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import com.asm.data.sources.local.entities.GameRoom

@Dao
interface GameDao {
    @Insert
    suspend fun insertAll(games: List<GameRoom>)
}