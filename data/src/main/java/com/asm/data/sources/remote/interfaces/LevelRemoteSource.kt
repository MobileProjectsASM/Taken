package com.asm.data.sources.remote.interfaces

import com.asm.domain.entities.Level

interface LevelRemoteSource {
    suspend fun geRangeLevels(initialRange: Int =  1, finalRange: Int): List<Level>
}