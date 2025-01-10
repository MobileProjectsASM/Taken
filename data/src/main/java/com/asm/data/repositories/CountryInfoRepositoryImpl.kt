package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.toSuccessful
import com.asm.domain.entities.toUnsuccessful
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
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

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>> {
        return try {
            countryInfoLocalSource.getCountriesInfoSortedByName().toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }

    override suspend fun downloadCountriesInfo(): Result<List<CountryInfo>> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return GeneralFailure.OtherError(GeneralErrorType.NETWORK_CONNECTION).toUnsuccessful()
            val countriesCallCodeResult = countryInfoRemoteSource.getCountriesCallCode()
            if (countriesCallCodeResult is Result.Unsuccessful) return countriesCallCodeResult
            val countriesCallCode = countriesCallCodeResult.asSuccessful().data
            countryInfoLocalSource.saveCountriesInfo(countriesCallCode)
            countriesCallCode.toSuccessful()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }
}