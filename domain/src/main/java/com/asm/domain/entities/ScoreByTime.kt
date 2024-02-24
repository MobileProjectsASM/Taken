package com.asm.domain.entities

import com.asm.domain.entities.interfaces.LevelScore
import kotlin.math.abs
import kotlin.math.roundToInt

class ScoreByTime: LevelScore {
    override fun getScore(level: Level, gameProcess : GameStatus.Process): Double = getScoreByResolutionTime(level, gameProcess.timePlaying)

    private fun getScoreByResolutionTime(level: Level, resolutionTime: Int): Double {
        val timeMetrics = level.timeMetrics
        if (resolutionTime <= timeMetrics.percentage100) return 100.0
        if (resolutionTime <= timeMetrics.percentage90) return 90.0
        if (resolutionTime <= timeMetrics.percentage80) return 80.0
        if (resolutionTime <= timeMetrics.percentage70) return 70.0
        if (resolutionTime <= timeMetrics.percentage60) return 60.0
        return 0.0
    }
}