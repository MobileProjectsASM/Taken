package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.data.sources.remote.interfaces.GameRemoteSource
import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Error
import com.asm.domain.repositories.GameRepository
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameLocalSource: GameLocalSource,
    private val gameRemoteSource: GameRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
): GameRepository {

    companion object {
        const val TAG = "GamerRepositoryImpl"
    }

    override suspend fun saveGamerGames(games: List<Game>, gamerId: String): Result<Completed> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Error.NetworkConnection.toFailure()
            gameRemoteSource.insertGames(games, gamerId)
            gameLocalSource.saveGamesByGamerId(games, gamerId)
            Completed.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Error.UnknownError.toFailure()
        }
    }
}