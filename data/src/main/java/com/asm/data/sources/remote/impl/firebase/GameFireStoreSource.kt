package com.asm.data.sources.remote.impl.firebase

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.GameRemoteSource
import com.asm.data.sources.remote.impl.firebase.mappers.GameMapper
import com.asm.domain.entities.Game
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameFireStoreSource @Inject constructor(
    private val fs: FirebaseFirestore,
    private val gameMapper: GameMapper
): GameRemoteSource {
    companion object {
        const val TAG = "GameFireStoreSource"
        const val GAME_COLLECTION = "games"
    }

    override suspend fun insertGames(games: List<Game>, gamerId: String) {
        try {
            val gamesFireStore = games.map(gameMapper::getGameFireStore)
            val collectionPath = fs.collection(GamerFirebaseSource.GAMER_COLLECTION).document(gamerId).collection(
                GAME_COLLECTION
            )
            fs.runBatch { batch ->
                gamesFireStore.forEach {
                    val gameReference = collectionPath.document(it.gameId)
                    batch.set(gameReference, it)
                }
            }.await()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to insert games remote source")
        }
    }
}