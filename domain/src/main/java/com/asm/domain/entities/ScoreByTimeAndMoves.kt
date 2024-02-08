package com.asm.domain.entities

import com.asm.domain.entities.interfaces.LevelScore
import kotlin.math.abs
import kotlin.math.roundToInt

class ScoreByTimeAndMoves: LevelScore {
    override fun getScore(level: Level, resolutionTime: Int, resolutionMovements: Int): Int = ((getScoreByResolutionTime(level, resolutionTime) + getScoreByMovements(level, resolutionMovements)) / 2).roundToInt()

    private fun getScoreByResolutionTime(level: Level, resolutionTime: Int): Double = if (level.maxTime > resolutionTime) {
        ((level.maxTime - resolutionTime) / level.maxTime.toDouble()) * 100.0
    } else {
        0.0
    }

    private fun getScoreByMovements(level: Level, resolutionMovements: Int): Int {
        val leftOverMovements = abs(level.minMovements - resolutionMovements)
        val penalty = (leftOverMovements / 3) * 10
        return if (penalty >= 100) 0 else 100 - penalty
    }
}