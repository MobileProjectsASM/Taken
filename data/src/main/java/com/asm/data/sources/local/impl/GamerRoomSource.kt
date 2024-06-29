package com.asm.data.sources.local.impl

import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.local.mappers.GamerMapper
import com.asm.domain.entities.Gamer
import javax.inject.Inject

class GamerRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val gamerMapper: GamerMapper
): GamerLocalSource {

    companion object {
        const val TAG = "GamerRoomSource"
    }
    override suspend fun saveGamer(gamer: Gamer) {
        try {
            takenDB.getGamerDao().insertGamer(gamerMapper.getGamerRoom(gamer))
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveGamer local source")
        }
    }

    override suspend fun getGamer(gamerId: String): Gamer? {
        try {
            return takenDB.getGamerDao().getGamerById(gamerId)?.let(gamerMapper::getGamer)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to getGamer local source")
        }
    }


    override suspend fun checkGamerExists(gamerId: String): Boolean {
        try {
            return takenDB.getGamerDao().gamerExists(gamerId) == 1
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to checkGamerExists local source")
        }
    }
}