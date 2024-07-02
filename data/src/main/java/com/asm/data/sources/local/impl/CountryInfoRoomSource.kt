package com.asm.data.sources.local.impl

import android.util.Log
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.local.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoRoomSource @Inject constructor(
    private val takenDB: TakenDB,
    private val countryInfoMapper: CountryInfoMapper
): CountryInfoLocalSource {
    companion object {
        const val TAG = "CountryInfoRoomSource"
    }

    override suspend fun getCountriesInfo(): List<CountryInfo> {
        try {
            return takenDB.getCountryInfoDao().getCountriesInfo().map(countryInfoMapper::getCountryInfo)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to getCountriesCallCode local source")
        }
    }

    override suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>) {
        try {
            val countriesInfoRoom = countriesInfo.map(countryInfoMapper::getCountryInfoRoom)
            val sorted = countriesInfoRoom.sortedBy { it.phoneCode }
            takenDB.getCountryInfoDao().saveCountriesInfo(sorted)
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to saveCountriesCallCode local source")
        }
    }
}