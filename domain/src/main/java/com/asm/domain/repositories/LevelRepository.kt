package com.asm.domain.repositories

import com.asm.domain.entities.Level
import com.asm.domain.errors.Failure
import com.asm.domain.utils.Either

interface LevelRepository {
    suspend fun getLevelByOrder(levelOrder: Int): Either<Failure, Level>
    suspend fun getRangeLevels(initialRange: Int =  1, finalRange: Int): Either<Failure, List<Level>>
}