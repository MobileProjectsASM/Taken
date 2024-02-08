package com.asm.domain.entities.interfaces

import com.asm.domain.entities.Level

interface LevelScore {
    fun getScore(level: Level, resolutionTime: Int, resolutionMovements: Int): Int
}