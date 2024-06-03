package com.asm.data.sources.remote.mappers

import com.asm.data.sources.remote.model.LevelFireStore
import com.asm.domain.entities.Box
import com.asm.domain.entities.Difficulty
import com.asm.domain.entities.Level
import com.asm.domain.entities.MovementsMetrics
import com.asm.domain.entities.TimeMetrics
import com.google.gson.Gson
import javax.inject.Inject

class LevelMapper @Inject constructor(
    private val gson: Gson
) {
    fun getLevel(levelFireStore: LevelFireStore): Level {
        val difficulty = when (levelFireStore.difficulty) {
            Difficulty.EASY.toString() -> Difficulty.EASY
            Difficulty.MEDIUM.toString() -> Difficulty.MEDIUM
            else -> Difficulty.HARD
        }
        val timeMetrics = gson.fromJson(levelFireStore.timeMetrics.toJson(), TimeMetrics::class.java)
        val movementsMetrics = gson.fromJson(levelFireStore.movementsMetrics.toJson(), MovementsMetrics::class.java)
        val response = levelFireStore.response.map { row ->
            row.map {
                gson.fromJson(it.toJson(), Box::class.java)
            }.toTypedArray()
        }.toTypedArray()

        return Level(
            levelFireStore.levelId,
            levelFireStore.levelName,
            levelFireStore.orderCriteria,
            levelFireStore.levelImage,
            difficulty,
            timeMetrics,
            movementsMetrics,
            response
        )
    }

    private fun Map<*, *>.toJson(): String = gson.toJson(this)
    /*fun getLevelFireStore(level: Level): LevelFireStore {
        val timeMetricsMap = gson.toMap(level.timeMetrics).asST<String, Int>()
        val movementMetricsMap = gson.toMap(level.movementsMetrics).asST<String, Int>()
        val responseMap = level.response.map { row ->
            row.map { box ->
                gson.toMap(box).asST<Int, Long>()
            }.toTypedArray()
        }.toTypedArray()


        return LevelFireStore(
            level.levelId,
            level.levelName,
            level.orderCriteria,
            level.levelImage,
            level.difficulty.toString(),
            timeMetricsMap,
            movementMetricsMap,
            responseMap
        )
    }

    private fun Gson.toMap(obj: Any): Map<*, *> {
        val json = toJson(obj)
        return fromJson(json, Map::class.java)
    }

    private fun <S, T> Map<*, *>.asST(): Map<S, T> {
        val mapStringAny = mutableMapOf<S, T>()
        for (entry in this) {
            val key = entry.key as S
            val value = entry.value as T
            mapStringAny[key] = value
        }
        return mapStringAny
    }*/
}