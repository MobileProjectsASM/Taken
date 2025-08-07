package com.asm.data.sources.remote.impl.rest

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.impl.rest.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure
import javax.inject.Inject

class CountryInfoRestServiceSource @Inject constructor(
    private val countryInfoClient: CountryInfoClient,
    private val countryInfoMapper: CountryInfoMapper
): CountryInfoRemoteSource {

    companion object {
        const val TAG = "CountryInfoRestService"
    }

    override suspend fun getCountriesCallCode(): Result<List<CountryInfo>, GeneralFailure> {
        return try {
            val response = countryInfoClient.getCountriesInfo()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, errorBody ?: "ServerError")
                return Result.Unsuccessful(GeneralFailure.ServerError(response.code(), "Server Error"))
            }
            val countries = response.body() ?: throw Exception("Empty response")
            val countriesInfo = countries.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countriesInfo)
        } catch(exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Result.Unsuccessful(GeneralFailure.Unknown)
        }
    }
}