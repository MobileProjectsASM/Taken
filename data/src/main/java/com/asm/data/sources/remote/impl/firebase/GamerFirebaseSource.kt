package com.asm.data.sources.remote.impl.firebase

import android.content.Context
import android.util.Log
import com.asm.data.R
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.data.sources.remote.impl.firebase.data.GamerFirebase
import com.asm.data.sources.remote.impl.firebase.data.GamerKeys
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.net.URL
import javax.inject.Inject

class GamerFirebaseSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val functions: FirebaseFunctions,
    private val fs: FirebaseFirestore,
    private val gson: Gson
) : GamerRemoteSource {

    companion object {
        const val TAG = "GamerFireStoreSource"
        const val GAMER_COLLECTION = "gamers"
    }

    override suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): String {
        try {
            val gamerFirebase = GamerFirebase(userId, gamerAlias, gamerAge, gamerCountry, "")
            val json = gson.toJson(gamerFirebase)
            val map = gson.fromJson(json, Map::class.java)
            val httpCallableResult = functions.getHttpsCallableFromUrl(URL(context.getString(R.string.create_gamer_cloud_function_url))).call(map).await()
            val data = httpCallableResult.data?.let { it as Map<*, *> } ?: throw Exception("Error data is null")
            val gamerId = if (data.contains(GamerKeys.GAMER_ID)) data[GamerKeys.GAMER_ID] else throw Exception("GamerId not found")
            return gamerId?.let { it as String } ?: throw Exception("GamerId is null")
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveGame remote source")
        }
    }

    override suspend fun checkGamerExists(gamerId: String): Boolean {
        try {
            val snapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            val gamer = snapshot.toObject<GamerFirebase>()
            return gamer != null
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to checkGamerExists remote source")
        }
    }

    override suspend fun updateGamerImage(gamerId: String, gamerImage: String) {
        try {
            val imageUpdate = mapOf(GamerKeys.GAMER_IMAGE to gamerImage)
            fs.collection(GAMER_COLLECTION).document(gamerId).update(imageUpdate).await()
        } catch(exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception;
        }
    }
}