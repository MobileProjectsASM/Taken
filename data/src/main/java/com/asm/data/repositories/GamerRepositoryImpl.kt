package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GamerRepositoryImpl @Inject constructor(
    private val gamerLocalSource: GamerLocalSource,
    private val gamerRemoteSource: GamerRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
) : GamerRepository {
    companion object {
        const val TAG = "GamerRepositoryImpl"
    }

    override suspend fun registerGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): Result<String, GeneralFailure> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.NetworkConnection)
            val gamerId = gamerRemoteSource.saveGamer(userId, gamerAlias, gamerAge, gamerCountry)
            Result.Successful(gamerId)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun updateGamerImage(
        gamerId: String,
        imageUrl: String
    ): Result<Unit, GeneralFailure> {
        return try {
            gamerRemoteSource.updateGamerImage(gamerId, imageUrl)
            Result.Successful(Unit)
        } catch(exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GeneralFailure> {
        return try {
            val gamerExists = gamerRemoteSource.checkGamerExists(gamerId)
            Result.Successful(gamerExists)
        } catch(exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }

    override suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralFailure> {
        return try {
            val gamer = gamerRemoteSource.getGamerById(gamerId)
            Result.Successful(gamer)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}