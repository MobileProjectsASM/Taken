package com.asm.data.sources.remote.interfaces

import com.asm.domain.entities.Level

interface LevelRemoteSource {
    suspend fun getLevelsByOrders(levelIds: List<Int>): List<Level>
}