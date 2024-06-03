package com.asm.data.sources.local.mappers

import com.asm.data.sources.local.entities.LevelRoom
import com.asm.domain.entities.Level
import com.google.gson.Gson
import javax.inject.Inject

class LevelMapper @Inject constructor(
    private val gson: Gson
) {

    fun getLevelRoom(level: Level): LevelRoom {
        val timeMetricsJson = gson.toJson(level.timeMetrics)
        val movementsMetricsJson = gson.toJson(level.movementsMetrics)
        val responseJson = gson.toJson(level.response)

        return LevelRoom(
            level.levelId,
            level.levelName,
            level.orderCriteria,
            level.levelImage,
            level.difficulty.toString(),
            timeMetricsJson,
            movementsMetricsJson,
            responseJson
        )
    }
}