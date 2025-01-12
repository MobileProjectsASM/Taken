package com.asm.data.sources.remote.impl.rest

import android.util.Log
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.impl.rest.mappers.CountryInfoMapper
import com.asm.data.sources.remote.impl.rest.data.CountryError
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.google.gson.Gson
import javax.inject.Inject

class CountryInfoRestServiceSource @Inject constructor(
    private val countryInfoClient: CountryInfoClient,
    private val countryInfoMapper: CountryInfoMapper,
    private val gson: Gson
): CountryInfoRemoteSource {

    companion object {
        const val TAG = "CountryInfoRestService"
    }

    override suspend fun getCountriesCallCode(): Result<List<CountryInfo>, GeneralFailure> {
        return try {
            val response = countryInfoClient.getCountriesInfo()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                if (errorBody.isNullOrBlank()) {
                    throw Exception("Unknown error")
                }
                val apiError = gson.fromJson(errorBody, CountryError::class.java)
                return Result.Unsuccessful(GeneralFailure.ServerError(apiError.status, apiError.message))
            }
            val countriesResponse = response.body() ?: throw Exception("Empty response")
            val countriesInfo = countriesResponse.countries.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countriesInfo)
        } catch(exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }
    }
}