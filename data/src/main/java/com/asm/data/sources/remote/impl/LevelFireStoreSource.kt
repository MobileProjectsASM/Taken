package com.asm.data.sources.remote.impl

import android.util.Log
import com.asm.data.sources.remote.interfaces.LevelRemoteSource
import com.asm.data.sources.remote.mappers.LevelMapper
import com.asm.data.sources.remote.model.LevelFireStore
import com.asm.domain.entities.Level
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LevelFireStoreSource @Inject constructor(
    private val fs: FirebaseFirestore,
    private val levelMapper: LevelMapper
): LevelRemoteSource {

    companion object {
        const val TAG = "LevelFireStoreSource"
        const val LEVEL_COLLECTION = "levels"
        const val LEVEL_ID = "levelId"
    }

    override suspend fun getLevelsByOrders(levelIds: List<Int>): List<Level> {
        try {
            val taskLevels = fs.collection(LEVEL_COLLECTION).whereIn(LEVEL_ID, levelIds).get()
            val documents = taskLevels.await().documents
            return documents.map {
                val levelFireStore = it.toObject<LevelFireStore>()!!
                levelMapper.getLevel(levelFireStore)
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to getLevelsByOrders remote source")
        }
    }

}