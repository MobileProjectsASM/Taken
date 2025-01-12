package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.remote.abstract_remotes.LevelRemoteSource
import com.asm.domain.entities.Level
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    private val levelRemoteSource: LevelRemoteSource,
    private val levelLocalSource: LevelLocalSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
): LevelRepository {

    companion object {
        const val TAG = "LevelRepositoryImpl"
    }

    override suspend fun downloadLevelsByOrderCriteria(ids: List<Int>): Result<List<Level>, GeneralFailure> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.NETWORK_CONNECTION))
            val levels = levelRemoteSource.getLevelsByOrders(ids)
            levelLocalSource.saveLevels(levels)
            Result.Successful(levels)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }

    }
}