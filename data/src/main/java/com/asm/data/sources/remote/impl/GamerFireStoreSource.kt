package com.asm.data.sources.remote.impl

import android.util.Log
import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.data.sources.remote.model.GamerFireStore as GamerFireStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GamerFireStoreSource @Inject constructor(
    private val fs: FirebaseFirestore
) : GamerRemoteSource {

    companion object {
        const val TAG = "GamerFireStoreSource"
        const val GAMER_COLLECTION = "gamers"
    }

    override suspend fun saveGamer(gamer: Gamer) {
        try {
            fs.collection(GAMER_COLLECTION).document(gamer.gamerId).set(gamer).await()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveGame remote source")
        }
    }

    override suspend fun checkGamerExists(gamerId: String): Boolean {
        try {
            val snapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            val gamer = snapshot.toObject<GamerFireStore>()
            return gamer != null
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to checkGamerExists remote source")
        }
    }
}