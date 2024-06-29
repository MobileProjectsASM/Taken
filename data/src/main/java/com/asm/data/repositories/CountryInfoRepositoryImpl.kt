package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.remote.interfaces.CountryInfoRemoteSource
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CountryInfoRepositoryImpl @Inject constructor(
    private val countryInfoLocalSource: CountryInfoLocalSource,
    private val countryInfoRemoteSource: CountryInfoRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
): CountryInfoRepository {

    companion object {
        const val TAG = "CountryRepositoryImpl"
    }

    override suspend fun getCountriesInfo(): Result<List<CountryInfo>> {
        return try {
            countryInfoLocalSource.getCountriesInfo().toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return Failure.UnknownFailure.toFailure()
        }
    }

    override suspend fun downloadCountriesInfo(): Result<List<CountryInfo>> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Failure.NetworkConnection.toFailure()
            val countriesCallCode = countryInfoRemoteSource.getCountriesCallCode()
            countryInfoLocalSource.saveCountriesInfo(countriesCallCode)
            countriesCallCode.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.UnknownFailure.toFailure()
        }
    }
}