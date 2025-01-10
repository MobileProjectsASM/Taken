package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.entities.toUnsuccessful
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.errors.GamerGeneralFailure
import com.asm.domain.errors.GeneralErrorType
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

    override suspend fun registerGamer(gamer: Gamer): Result<Completed> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return GeneralFailure.OtherError(GeneralErrorType.NETWORK_CONNECTION).toUnsuccessful()
            gamerRemoteSource.saveGamer(gamer)
            gamerLocalSource.saveGamer(gamer)
            Completed.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }

    override suspend fun checkIfGamerExists(gamerId: String): Result<Boolean> {
        return try {
            val gamerExists = gamerLocalSource.checkGamerExists(gamerId)
            gamerExists.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }

    override suspend fun getGamerById(gamerId: String): Result<Gamer> {
        return try {
            gamerLocalSource.getGamer(gamerId)?.toSuccessful() ?: GamerGeneralFailure.GamerNotExists.toUnsuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }

}