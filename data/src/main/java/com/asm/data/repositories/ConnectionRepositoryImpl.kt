package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.ConnectionRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class ConnectionRepositoryImpl @Inject constructor(
    private val connectionSource: ConnectionSource,
    private val logger: Logger
) : ConnectionRepository {

    companion object {
        const val TAG = "ConnectionRepositoryImpl"
    }

    override suspend fun isNetworkAvailable(): Result<Boolean> {
        return try {
            connectionSource.isNetworkAvailable().toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.NetworkConnection.toFailure()
        }
    }
}