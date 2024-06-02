package com.asm.domain.repositories

import com.asm.domain.entities.Level
import com.asm.domain.entities.Result

interface LevelRepository {
    suspend fun downloadLevelsByOrderCriteria(ids: List<Int>): Result<List<Level>>
}