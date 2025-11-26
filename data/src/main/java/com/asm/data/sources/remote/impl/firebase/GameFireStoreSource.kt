package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.GameRemoteSource
import com.asm.data.sources.remote.impl.firebase.data.GameFireStore
import com.asm.data.sources.remote.impl.firebase.mappers.GameMapper
import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameFireStoreSource @Inject constructor(
    private val fs: FirebaseFirestore,
    private val gameMapper: GameMapper
) : GameRemoteSource {
    companion object {
        const val TAG = "GameFireStoreSource"
        const val GAME_COLLECTION = "games"
    }

    override suspend fun getGamesByGamerId(gamerId: String): Result<List<Game>, GeneralError> {
        return try {
            val querySnapshot = fs.collection(GamerFirebaseSource.GAMER_COLLECTION).document(
                gamerId
            ).collection(GAME_COLLECTION).get().await()
            return if (querySnapshot != null) {
                val games = querySnapshot.toObjects(GameFireStore::class.java)
                    .map(gameMapper::gameSourceToDomain)
                Result.Successful(games)
            } else {
                Log.e(TAG, "QuerySnapshot is null")
                GeneralError.ServerError().toUnsuccessful()
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected error to get gamer from firestore", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}