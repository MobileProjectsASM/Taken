package com.asm.data.sources.remote.impl

import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.domain.entities.Gamer

class GamerRemoteSourceImpl : GamerRemoteSource {
    override suspend fun saveGamer(gamer: Gamer) {
        TODO("Not yet implemented")
    }

    override suspend fun checkGamerExists(gamerId: String): Boolean {
        TODO("Not yet implemented")
    }
}