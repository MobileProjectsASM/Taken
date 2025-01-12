package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Completed
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

    override suspend fun registerGamer(gamer: Gamer): Result<Completed, GeneralFailure> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.NETWORK_CONNECTION))
            gamerRemoteSource.saveGamer(gamer)
            gamerLocalSource.saveGamer(gamer)
            Result.Successful(Completed)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }
    }

    override suspend fun checkIfGamerExists(gamerId: String): Result<Boolean, GeneralFailure> {
        return try {
            val gamerExists = gamerLocalSource.checkGamerExists(gamerId)
            Result.Successful(gamerExists)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }
    }

    override suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralFailure> {
        return try {
            Result.Successful(gamerLocalSource.getGamer(gamerId))
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }
    }

}