package com.asm.data.repositories

import android.database.sqlite.SQLiteException
import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure
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

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>, Failure> {
        return try {
            countryInfoLocalSource.getCountriesInfoSortedByName()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            val failure = when (exception) {
                is SQLiteException -> Failure.RepositoryFailure.LOCAL_SOURCE_FAILURE
                else -> Failure.UnexpectedFailure
            }
            Result.Unsuccessful(failure)
        }
    }

    override suspend fun downloadCountriesInfo(): Result<Unit, Failure> {
        return try {
            connectionSource.ifConnectionIsAvailableRun {
                val countriesInfo = when (val countriesCallCodeResult =
                    countryInfoRemoteSource.getCountriesCallCode()) {
                    is Result.Successful<List<CountryInfo>> -> countriesCallCodeResult.data
                    is Result.Unsuccessful<Failure> -> return@ifConnectionIsAvailableRun countriesCallCodeResult
                }
                countryInfoLocalSource.saveCountriesInfo(countriesInfo)
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            val failure = when (exception) {
                is SQLiteException -> Failure.RepositoryFailure.LOCAL_SOURCE_FAILURE
                else -> Failure.UnexpectedFailure
            }
            Result.Unsuccessful(failure)
        }
    }
}