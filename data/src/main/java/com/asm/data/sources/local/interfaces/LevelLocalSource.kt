package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.Level

interface LevelLocalSource {
    suspend fun saveLevels(levels: List<Level>)
}