package com.asm.data.sources.local.impl

import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.local.mappers.LevelMapper
import com.asm.domain.entities.Level
import javax.inject.Inject

class LevelRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val levelMapper: LevelMapper
): LevelLocalSource {

    companion object {
        const val TAG = "LevelRoomSource"
    }

    override suspend fun saveLevels(levels: List<Level>) {
        try {
            val levelsRoom = levels.map(levelMapper::getLevelRoom)
            takenDB.getLevelDao().insertLevels(levelsRoom)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveLevels local source")
        }
    }
}