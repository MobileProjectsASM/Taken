package com.asm.data.repositories

import android.util.Log
import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Error
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
            if (!connectionSource.isNetworkAvailable()) return Error.NetworkConnection.toFailure()
            gamerRemoteSource.saveGamer(gamer)
            gamerLocalSource.saveGamer(gamer)
            Completed.toSuccessful()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Error.UnknownError.toFailure()
        }
    }

    override suspend fun checkIfGamerExists(gamerId: String): Result<Boolean> {
        return try {
            val gamerExists = gamerLocalSource.checkGamerExists(gamerId)
            gamerExists.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Error.UnknownError.toFailure()
        }
    }

}