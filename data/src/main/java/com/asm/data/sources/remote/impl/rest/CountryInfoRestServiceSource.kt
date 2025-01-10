package com.asm.data.sources.remote.impl.rest

import android.util.Log
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.mappers.CountryInfoMapper
import com.asm.data.sources.remote.model.CountryError
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.toSuccessful
import com.asm.domain.entities.toUnsuccessful
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

    override suspend fun getCountriesCallCode(): Result<List<CountryInfo>> {
        return try {
            val response = countryInfoClient.getCountriesInfo()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                if (errorBody.isNullOrBlank()) {
                    throw Exception("Unknown error")
                }
                val apiError = gson.fromJson(errorBody, CountryError::class.java)
                return GeneralFailure.ServerError(apiError.status, apiError.message).toUnsuccessful()
            }
            val countriesResponse = response.body() ?: throw Exception("Empty response")
            val countriesInfo = countriesResponse.countries.map(countryInfoMapper::getCountryInfo)
            countriesInfo.toSuccessful()
        } catch(exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            GeneralFailure.OtherError(GeneralErrorType.UNKNOWN).toUnsuccessful()
        }
    }
}