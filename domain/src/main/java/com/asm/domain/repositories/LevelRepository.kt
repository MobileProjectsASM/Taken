package com.asm.domain.repositories

import com.asm.domain.entities.Level
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface LevelRepository {
    suspend fun downloadLevelsByOrderCriteria(ids: List<Int>): Result<List<Level>, GeneralError>
}