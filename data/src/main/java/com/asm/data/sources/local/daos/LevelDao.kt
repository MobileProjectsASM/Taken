package com.asm.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import com.asm.data.sources.local.entities.LevelRoom

@Dao
interface LevelDao {
    @Insert
    suspend fun insertLevels(levels: List<LevelRoom>)
}