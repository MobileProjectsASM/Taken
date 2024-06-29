package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryLocalSource
import com.asm.data.sources.remote.interfaces.CountryRemoteSource
import com.asm.domain.entities.CountryCallCode
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.CountryRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val countryLocalSource: CountryLocalSource,
    private val countryRemoteSource: CountryRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
): CountryRepository {

    companion object {
        const val TAG = "CountryRepositoryImpl"
    }

    override suspend fun getCountriesCallCode(): Result<List<CountryCallCode>> {
        return try {
            countryLocalSource.getCountriesCallCode().toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return Failure.UnknownFailure.toFailure()
        }
    }

    override suspend fun downloadCountriesCallCode(): Result<List<CountryCallCode>> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return Failure.NetworkConnection.toFailure()
            val countriesCallCode = countryRemoteSource.getCountriesCallCode()
            countryLocalSource.saveCountriesCallCode(countriesCallCode)
            countriesCallCode.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.UnknownFailure.toFailure()
        }
    }
}