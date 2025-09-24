package com.asm.data.sources.remote.impl.firebase

import android.content.Context
import android.util.Log
import com.asm.data.R
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.data.sources.remote.impl.firebase.data.GamerFirebase
import com.asm.data.sources.remote.impl.firebase.data.GamerKeys
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GamerError
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toGamerError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.IOException
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

    override suspend fun getGamerById(gamerId: String): Result<Gamer, GamerError> {
        return try {
            val documentSnapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            val gamerFirebase = documentSnapshot.toObject(GamerFirebase::class.java)
                ?: return GamerError.GamerNotExists.toUnsuccessful()
            val gamer = Gamer(
                gamerId = gamerFirebase.gamerId,
                gamerNickName = gamerFirebase.gamerNickName,
                gamerAge = gamerFirebase.gamerAge,
                gamerCountry = gamerFirebase.gamerCountry,
                gamerImage = gamerFirebase.gamerImage
            )
            Result.Successful(gamer)
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toGamerError().toUnsuccessful()
        }
    }

    override suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): Result<String, GamerError> {
        return try {
            val gamerFirebase = GamerFirebase(userId, gamerAlias, gamerAge, gamerCountry, "")
            val json = gson.toJson(gamerFirebase)
            val map = gson.fromJson(json, Map::class.java)
            val httpCallableResult =
                functions.getHttpsCallableFromUrl(URL(context.getString(R.string.create_gamer_cloud_function_url)))
                    .call(map).await()
            val data = httpCallableResult.data?.let { it as Map<*, *> }
                ?: return GeneralError.ServerError("There isn't data").toGamerError()
                    .toUnsuccessful()
            data[GamerKeys.GAMER_ID]?.let {
                Result.Successful(it as String)
            } ?: GeneralError.ServerError("Error to save gamer").toGamerError().toUnsuccessful()
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            when (exception) {
                is IOException -> GeneralError.NetworkError.toGamerError().toUnsuccessful()
                is FirebaseFunctionsException -> GeneralError.ServerError(
                    exception.message ?: "Unknown Error"
                ).toGamerError().toUnsuccessful()
                else -> GeneralError.Unknown.toGamerError().toUnsuccessful()
            }
        }
    }

    override suspend fun checkGamerExists(gamerId: String): Result<Boolean, GamerError> {
        return try {
            val snapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            Result.Successful(snapshot.exists())
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toGamerError().toUnsuccessful()
        }
    }

    override suspend fun updateGamerImage(
        gamerId: String,
        gamerImage: String
    ): Result<Unit, GamerError> {
        return try {
            val imageUpdate = mapOf(GamerKeys.GAMER_IMAGE to gamerImage)
            fs.collection(GAMER_COLLECTION).document(gamerId).update(imageUpdate).await()
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            GeneralError.Unknown.toGamerError().toUnsuccessful()
        }
    }
}