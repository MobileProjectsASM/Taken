package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.AuthenticationFailure
import com.asm.domain.errors.CommonFailure
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
        const val TAG = "country-repository"
    }

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>, CommonFailure> {
        return try {
            countryInfoLocalSource.getCountriesInfoSortedByName()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }

    override suspend fun downloadCountriesInfo(): Result<Unit, CommonFailure> {
        return try {
            connectionSource.ifConnectionIsAvailableRun {
                val countriesInfo = when (val countriesCallCodeResult =
                    countryInfoRemoteSource.getCountriesCallCode()) {
                    is Result.Successful<List<CountryInfo>> -> countriesCallCodeResult.data
                    is Result.Unsuccessful<AuthenticationFailure> -> return@ifConnectionIsAvailableRun countriesCallCodeResult
                }
                countryInfoLocalSource.saveCountriesInfo(countriesInfo)
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }

    private suspend fun <T> ConnectionSource.ifConnectionIsAvailableRun(execute: suspend () -> Result<T, CommonFailure>): Result<T, CommonFailure> {
        return if (isNetworkAvailable()) execute()
        else Result.Unsuccessful(CommonFailure.NETWORK_CONNECTION)
    }
}