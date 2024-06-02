package com.asm.data.sources.local.impl

import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.local.mappers.GamerMapper
import com.asm.domain.entities.Gamer
import javax.inject.Inject

class GamerRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val gamerMapper: GamerMapper
): GamerLocalSource {
    override suspend fun saveGamer(gamer: Gamer) {
        takenDB.getGamerDao().insertGamer(gamerMapper.getGamer(gamer))
    }

    override suspend fun getGamer(gamerId: String): Gamer {
        val gamerDomain = takenDB.getGamerDao().getGamerById(gamerId)
        return gamerMapper.toGamerDomain(gamerDomain)
    }

    override suspend fun checkGamerExists(gamerId: String): Boolean {
        return takenDB.getGamerDao().gamerExists(gamerId) == 1
    }
}