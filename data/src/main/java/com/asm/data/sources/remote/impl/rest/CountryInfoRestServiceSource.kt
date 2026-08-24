package com.asm.data.sources.remote.impl.rest

import android.util.Log
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.impl.rest.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.ApiServiceException
import com.asm.domain.errors.CommonFailure
import javax.inject.Inject

class CountryInfoRestServiceSource @Inject constructor(
    private val countryInfoClient: CountryInfoClient,
    private val countryInfoMapper: CountryInfoMapper
) : CountryInfoRemoteSource {

    companion object {
        const val TAG = "country-info-rest-service"
    }

    override suspend fun getCountriesCallCode(): Result<List<CountryInfo>, CommonFailure> {
        val response = try {
            countryInfoClient.getCountriesInfo()
        } catch (exception: Exception) {
            Log.e(TAG, exception.message, exception)
            return Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }

        return if (response.isSuccessful) {
            val countries = response.body()
                ?: throw ApiServiceException("Body is null despite api rest response is successful")
            val countriesInfo = countries.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countriesInfo)
        } else {
            val errorBody = response.errorBody()?.string() ?: "No details"
            val message = response.message() ?: "No message"
            Log.e(TAG, "${response.code()}: $message")
            Log.e(TAG, "details: $errorBody")
            Result.Unsuccessful(CommonFailure.REPOSITORY_FAILURE)
        }
    }
}