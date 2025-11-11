package com.asm.data.sources.remote.impl.firebase

import android.content.Context
import android.util.Log
import com.asm.data.R
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.data.sources.remote.impl.firebase.data.GamerFirebase
import com.asm.data.sources.remote.impl.firebase.data.GamerKeys
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
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

    override suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralError> {
        return try {
            val documentSnapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            val gamerFirebase = documentSnapshot.toObject(GamerFirebase::class.java)
                ?: return Result.Successful(null)
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
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String
    ): Result<String, GeneralError> {
        return try {
            val gamerFirebase = GamerFirebase(userId, gamerAlias, gamerAge, gamerCountry, "")
            val json = gson.toJson(gamerFirebase)
            val map = gson.fromJson(json, Map::class.java)
            val httpCallableResult =
                functions.getHttpsCallableFromUrl(URL(context.getString(R.string.create_gamer_cloud_function_url)))
                    .call(map).await()
            val data = httpCallableResult.data?.let { it as Map<*, *> }
                ?: return GeneralError.ServerError().toUnsuccessful().also {
                    Log.e(TAG, "data response is null")
                }
            data[GamerKeys.GAMER_ID]?.let {
                Result.Successful(it as String)
            } ?: GeneralError.ServerError().toUnsuccessful().also {
                Log.e(TAG, "gamerId response is invalid")
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            when (exception) {
                is IOException -> GeneralError.NetworkError.toUnsuccessful()
                is FirebaseFunctionsException -> handleFirebaseFunctionException(exception)
                    .toUnsuccessful()

                else -> GeneralError.Unknown.toUnsuccessful()
            }
        }
    }

    override suspend fun checkGamerExists(gamerId: String): Result<Boolean, GeneralError> {
        return try {
            val snapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            Result.Successful(snapshot.exists())
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun updateGamerImage(
        gamerId: String,
        gamerImage: String
    ): Result<Unit, GeneralError> {
        return try {
            val imageUpdate = mapOf(GamerKeys.GAMER_IMAGE to gamerImage)
            fs.collection(GAMER_COLLECTION).document(gamerId).update(imageUpdate).await()
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private fun handleFirebaseFunctionException(firebaseFunctionsException: FirebaseFunctionsException): GeneralError {
        return when (val code = firebaseFunctionsException.code) {
            FirebaseFunctionsException.Code.INVALID_ARGUMENT, FirebaseFunctionsException.Code.NOT_FOUND, FirebaseFunctionsException.Code.UNAUTHENTICATED, FirebaseFunctionsException.Code.PERMISSION_DENIED, FirebaseFunctionsException.Code.CANCELLED -> GeneralError.ClientError(
                code.name
            )

            else -> GeneralError.ServerError(code.name)
        }
    }
}