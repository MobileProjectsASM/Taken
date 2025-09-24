package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GamerError
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toGamerError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import javax.inject.Inject

class GamerRepositoryImpl @Inject constructor(
    private val gamerRemoteSource: GamerRemoteSource,
    private val connectionSource: ConnectionSource,
) : GamerRepository {
    companion object {
        const val TAG = "GamerRepositoryImpl"
    }

    override suspend fun registerGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): Result<String, GamerError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toGamerError().toUnsuccessful()
        return gamerRemoteSource.saveGamer(userId, gamerAlias, gamerAge, gamerCountry)
    }

    override suspend fun updateGamerImage(
        gamerId: String,
        imageUrl: String
    ): Result<Unit, GamerError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toGamerError().toUnsuccessful()
        return gamerRemoteSource.updateGamerImage(gamerId, imageUrl)
    }

    override suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GamerError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toGamerError().toUnsuccessful()
        return gamerRemoteSource.checkGamerExists(gamerId)
    }

    override suspend fun getGamerById(gamerId: String): Result<Gamer, GamerError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toGamerError().toUnsuccessful()
        return gamerRemoteSource.getGamerById(gamerId)
    }
}