package com.asm.data.sources.local.impl

import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.local.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import javax.inject.Inject

class CountryInfoRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val countryInfoMapper: CountryInfoMapper
): CountryInfoLocalSource {
    companion object {
        const val TAG = "CountryInfoRoomSource"
    }

    override suspend fun getCountriesInfoSortedByName(ascending: Boolean): Result<List<CountryInfo>, Failure> {
        return try {
            val countries = when {
                ascending -> takenDB.getCountryInfoDao().getCountriesInfoSortedByNameAsc()
                else -> takenDB.getCountryInfoDao().getCountriesInfoSortedByNameDesc()
            }.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countries)
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>): Result<Unit, Failure> {
        return try {
            val countriesInfoRoom = countriesInfo.map(countryInfoMapper::getCountryInfoRoom)
            val sorted = countriesInfoRoom.sortedBy { it.phoneCode }
            takenDB.getCountryInfoDao().saveCountriesInfo(sorted)
            Result.Successful(Unit)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}