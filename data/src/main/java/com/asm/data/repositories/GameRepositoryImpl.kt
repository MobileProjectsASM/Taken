package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.data.sources.remote.abstract_remotes.GameRemoteSource
import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
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

    override suspend fun saveGamerGames(games: List<Game>, gamerId: String): Result<Completed, GeneralFailure> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Result.Unsuccessful(GeneralFailure.NetworkConnection)
            gameRemoteSource.insertGames(games, gamerId)
            gameLocalSource.saveGamesByGamerId(games, gamerId)
            Result.Successful(Completed)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}