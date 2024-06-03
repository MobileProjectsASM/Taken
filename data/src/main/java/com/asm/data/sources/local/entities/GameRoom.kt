package com.asm.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameRoom(
    @PrimaryKey
    @ColumnInfo(name = "game_id") val gameId: String,
    @ColumnInfo(name = "gamer_id") val gamerId: String,
    @ColumnInfo(name = "level_id") val levelId: String,
    @ColumnInfo(name = "game_status") val gameStatus: String,
    @ColumnInfo(name = "moves_made") val movesMade: Int? = null,
    @ColumnInfo(name = "time_playing") val timePlaying: Int? = null,
    val score: Double? = null
)
