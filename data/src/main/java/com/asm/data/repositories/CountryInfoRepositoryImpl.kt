package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CountryInfoRepositoryImpl @Inject constructor(
    private val countryInfoLocalSource: CountryInfoLocalSource,
    private val countryInfoRemoteSource: CountryInfoRemoteSource,
    private val connectionSource: ConnectionSource,
    private val logger: Logger
) : CountryInfoRepository {

    companion object {
        const val TAG = "CountryRepositoryImpl"
    }

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>, GeneralError> {
        return try {
            countryInfoLocalSource.getCountriesInfoSortedByName()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun downloadCountriesInfo(): Result<Unit, GeneralError> {
        return try {
            if (!connectionSource.isNetworkAvailable()) return GeneralError.NetworkError.toUnsuccessful()
            when (val countriesCallCodeResult = countryInfoRemoteSource.getCountriesCallCode()) {
                is Result.Successful<List<CountryInfo>> -> countryInfoLocalSource.saveCountriesInfo(
                    countriesCallCodeResult.data
                )
                is Result.Unsuccessful<GeneralError> -> countriesCallCodeResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}