package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GamerRepositoryImpl @Inject constructor(
    private val gamerRemoteSource: GamerRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
) : GamerRepository {
    companion object {
        const val TAG = "GamerRepositoryImpl"
    }

    override suspend fun registerGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String
    ): Result<String, GeneralError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toUnsuccessful()
        return gamerRemoteSource.saveGamer(
            userId,
            gamerAlias,
            gamerAge,
            gamerCountry,
            gamerCountryFlag,
            gamerImage
        )
    }

    override suspend fun updateGamer(
        userId: String,
        gamerAlias: String,
        gamerAge: Int,
        gamerCountry: String,
        gamerCountryFlag: String?,
        gamerImage: String
    ): Result<String, GeneralError> {
        if (!connectionSource.isNetworkAvailable())
            return GeneralError.NetworkError.toUnsuccessful()
        return gamerRemoteSource.updateGamer(
            userId,
            gamerAlias,
            gamerAge,
            gamerCountry,
            gamerCountryFlag,
            gamerImage
        )
    }

    override suspend fun updateGamerImage(
        gamerId: String,
        imageUrl: String
    ): Result<Unit, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) GeneralError.NetworkError.toUnsuccessful()
            else gamerRemoteSource.updateGamerImage(gamerId, imageUrl)
        } catch (e: Exception) {
            logger.logE(MultimediaRepositoryImpl.TAG, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun verifyGamerExists(gamerId: String): Result<Boolean, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) GeneralError.NetworkError.toUnsuccessful()
            else gamerRemoteSource.checkGamerExists(gamerId)
        } catch (e: Exception) {
            logger.logE(MultimediaRepositoryImpl.TAG, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun getGamerById(gamerId: String): Result<Gamer?, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) GeneralError.NetworkError.toUnsuccessful()
            else gamerRemoteSource.getGamerById(gamerId)
        } catch (e: Exception) {
            logger.logE(MultimediaRepositoryImpl.TAG, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}