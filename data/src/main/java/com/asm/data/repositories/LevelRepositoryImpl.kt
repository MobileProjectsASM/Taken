package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.remote.interfaces.LevelRemoteSource
import com.asm.domain.entities.Level
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Failure
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

    override suspend fun downloadLevelsByOrderCriteria(ids: List<Int>): Result<List<Level>> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Failure.NetworkConnection.toFailure()
            val levels = levelRemoteSource.getLevelsByOrders(ids)
            levelLocalSource.saveLevels(levels)
            levels.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.UnknownFailure.toFailure()
        }

    }
}