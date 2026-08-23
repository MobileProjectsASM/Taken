package com.asm.data.sources.remote.impl.firebase

import android.content.Context
import android.util.Log
import com.asm.data.R
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.data.sources.remote.impl.firebase.data.GamerFirebase
import com.asm.data.sources.remote.impl.firebase.data.GamerKeys
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
        const val TAG = "GamerFirebaseSource"
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
                gamerCountryFlag = gamerFirebase.gamerCountryFlag,
                gamerImage = gamerFirebase.gamerImage
            )
            Result.Successful(gamer)
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected error to get gamer by Id", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun saveGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String
    ): Result<String, GeneralError> {
        return try {
            val gamerFirebase = GamerFirebase(userId, gamerAlias, gamerAge, gamerCountry, gamerCountryFlag, gamerImage)
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
            Log.e(TAG, "Unexpected save gamer", exception)
            when (exception) {
                is IOException -> GeneralError.ConnectionError.toUnsuccessful()
                is FirebaseFunctionsException -> handleFirebaseFunctionException(exception)
                    .toUnsuccessful()

                else -> GeneralError.Unknown.toUnsuccessful()
            }
        }
    }

    override suspend fun updateGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String
    ): Result<String, GeneralError> {
        return try {
            val gamerReference = fs.collection(GAMER_COLLECTION).document(userId)
            val dataToUpdate = mapOf(
                "gamerNickName" to gamerAlias,
                "gamerAge" to gamerAge,
                "gamerCountry" to gamerCountry,
                "gamerCountryFlag" to gamerCountryFlag,
                "gamerImage" to gamerImage
            )
            gamerReference.update(dataToUpdate).await()
            Result.Successful(userId)
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected save gamer", exception)
            when (exception) {
                is IOException -> GeneralError.ConnectionError.toUnsuccessful()
                is FirebaseFirestoreException -> handleFirebaseFireStoreException(exception)
                    .toUnsuccessful()

                else -> GeneralError.Unknown.toUnsuccessful()
            }
        }
    }

    override suspend fun checkGamerExists(gamerId: String): Result<Boolean, Failure> {
        return try {
            val snapshot = fs.collection(GAMER_COLLECTION).document(gamerId).get().await()
            Result.Successful(snapshot.exists())
        } catch (exception: Exception) {
            Log.e(TAG, "Error to check gamer exists", exception)
            val failure = when (exception) {
                is FirebaseException -> Failure.RepositoryFailure.REMOTE_SOURCE_FAILURE
                else -> Failure.UnexpectedFailure
            }
            return Result.Unsuccessful(failure)
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
            Log.e(TAG, "Unexpected error to update gamer image", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun deleteGamer(gamerId: String): Result<Unit, GeneralError> {
        return try {
            val gamerReference = fs.collection(GAMER_COLLECTION).document(gamerId)
            gamerReference.delete().await()
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected error to delete gamer", exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private fun handleFirebaseFirestoreException(exception: FirebaseFirestoreException) {
        return when (val code = exception.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> Failure.RepositoryFailure.DATA_SOURCE_FAILURE
            FirebaseFirestoreException.Code.UNAVAILABLE -> Failure.SystemFailure.NETWORK_CONNECTION
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> {
                println("Error: Servidor saturado (Cuota excedida).")
            }
            else -> {
                println("Error de base de datos: ${e.message}")
            }
        }
    }

    private fun handleFirebaseFireStoreException(firebaseFireStoreException: FirebaseFirestoreException): GeneralError {
        return when (val code = firebaseFireStoreException.code) {
            FirebaseFirestoreException.Code.NOT_FOUND, FirebaseFirestoreException.Code.PERMISSION_DENIED, FirebaseFirestoreException.Code.UNAVAILABLE, FirebaseFirestoreException.Code.ALREADY_EXISTS, FirebaseFirestoreException.Code.INVALID_ARGUMENT -> GeneralError.ClientError(
                code.name
            )

            else -> GeneralError.ServerError(code.name)
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