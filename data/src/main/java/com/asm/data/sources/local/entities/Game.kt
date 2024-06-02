package com.asm.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "games", primaryKeys = ["gamer_id, level_id"])
data class Game(
    @ColumnInfo(name = "gamer_id") val gamerId: String,
    @ColumnInfo(name = "level_id") val levelId: String,
    @ColumnInfo(name = "game_status") val gameStatus: String,
    @ColumnInfo(name = "moves_made") val movesMade: Int?,
    @ColumnInfo(name = "time_playing") val timePlaying: Int?,
    val score: Double?
)
