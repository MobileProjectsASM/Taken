package com.asm.domain.entities.interfaces

import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Level

interface LevelScore {
    fun getScore(level: Level, gameWin: GameStatus.Process): Double
}