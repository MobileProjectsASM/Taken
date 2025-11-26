package com.asm.data.repositories

import com.asm.data.sources.remote.impl.firebase.GameFireStoreSource
import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GameRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val gameFireStore: GameFireStoreSource
): GameRepository {

    companion object {
        const val TAG = "GameRepositoryImpl"
    }

    override suspend fun getGamesByGamer(gamerId: String): Result<List<Game>, GeneralError> {
        return try {
            gameFireStore.getGamesByGamerId(gamerId)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}