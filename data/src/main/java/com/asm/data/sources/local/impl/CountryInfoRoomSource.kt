package com.asm.data.sources.local.impl

import android.database.sqlite.SQLiteException
import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.local.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.CommonFailure
import javax.inject.Inject

class CountryInfoRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val countryInfoMapper: CountryInfoMapper
): CountryInfoLocalSource {
    companion object {
        const val TAG = "country-info-room"
    }

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>, CommonFailure> {
        return try {
            val countries = when {
                ascending -> takenDB.getCountryInfoDao().getCountriesInfoSortedByNameAsc()
                else -> takenDB.getCountryInfoDao().getCountriesInfoSortedByNameDesc()
            }.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countries)
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            val failure = when (exception) {
                is SQLiteException -> CommonFailure.REPOSITORY_FAILURE
                else -> CommonFailure.UNEXPECTED_FAILURE
            }
            Result.Unsuccessful(failure)
        }
    }

    override suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>): Result<Unit, CommonFailure> {
        return try {
            val countriesInfoRoom = countriesInfo.map(countryInfoMapper::getCountryInfoRoom)
            val sorted = countriesInfoRoom.sortedBy { it.phoneCode }
            takenDB.getCountryInfoDao().saveCountriesInfo(sorted)
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            val failure = when (exception) {
                is SQLiteException -> CommonFailure.REPOSITORY_FAILURE
                else -> CommonFailure.UNEXPECTED_FAILURE
            }
            Result.Unsuccessful(failure)
        }
    }
}