package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.Level

interface LevelRemoteSource {
    suspend fun getLevelsByOrders(levelIds: List<Int>): List<Level>
}