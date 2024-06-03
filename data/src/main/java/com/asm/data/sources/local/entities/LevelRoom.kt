package com.asm.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelRoom(
    @PrimaryKey
    @ColumnInfo(name = "level_id") val levelId: String,
    @ColumnInfo(name = "level_name") val levelName: String,
    @ColumnInfo(name = "order_criteria") val orderCriteria: Int,
    @ColumnInfo(name = "level_image") val levelImage: String,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "time_metrics") val timeMetrics: String,
    @ColumnInfo(name = "movements_metrics") val movementsMetrics: String,
    @ColumnInfo(name = "response") val response: String
)