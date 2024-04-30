package com.asm.data.repositories

import android.util.Log
import com.asm.data.sources.hardware.Connection
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.domain.entities.Game
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GamerFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Either
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight
import javax.inject.Inject

class GamerRepositoryImpl @Inject constructor(
    val gamerLocalSource: GamerLocalSource,
    val gamerRemoteSource: GamerRemoteSource,
    val connection: Connection
) : GamerRepository {
    companion object {
        const val TAG = "GamerRepositoryImpl"
    }

    override suspend fun registerGamer(gamer: Gamer): Either<Failure, Completed> {
        return try {
            if (!connection.thereIsInternetConnection()) return Failure.NetworkConnection.toLeft()
            gamerRemoteSource.saveGamer(gamer)
            gamerLocalSource.saveGamer(gamer)
            Completed.toRight()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Failure.UnknownError.toLeft()
        }
    }

    override suspend fun checkIfGamerExists(gamerId: String): Either<Failure, Boolean> {
        return try {
            val gamerExists = if (connection.thereIsInternetConnection()) {
                gamerRemoteSource.checkGamerExists(gamerId)
            } else {
                gamerLocalSource.checkGamerExists(gamerId)
            }
            gamerExists.toRight()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Failure.UnknownError.toLeft()
        }
    }

    override suspend fun getGamerById(gamerId: String): Either<Failure, Gamer> {
        return try {
            val gamer = gamerLocalSource.getGamer(gamerId)
                ?: return GamerFailure.GamerNotExists.toLeft()
            gamer.toRight()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Failure.UnknownError.toLeft()
        }
    }

}