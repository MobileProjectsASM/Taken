package com.asm.data.sources.remote.impl.rest

import android.content.Context
import android.util.Log
import com.asm.data.R
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.impl.rest.mappers.CountryInfoMapper
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import javax.inject.Inject

class CountryInfoRestServiceSource @Inject constructor(
    private val context: Context,
    private val countryInfoClient: CountryInfoClient,
    private val countryInfoMapper: CountryInfoMapper
): CountryInfoRemoteSource {

    companion object {
        const val TAG = "CountryInfoRestService"
    }

    override suspend fun getCountriesCallCode(): Result<List<CountryInfo>, GeneralError> {
        return try {
            val response = countryInfoClient.getCountriesInfo()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "No details"
                val message = response.message() ?: "No message"
                Log.e(TAG, "${response.code()}: $message")
                Log.e(TAG, "details: $errorBody")
                return GeneralError.ServerError("${response.code()}: $message").toUnsuccessful()
            }
            val countries = response.body() ?: return GeneralError.ServerError(context.getString(R.string.err_server_response)).toUnsuccessful()
            val countriesInfo = countries.map(countryInfoMapper::getCountryInfo)
            Result.Successful(countriesInfo)
        } catch(exception: Exception) {
            Log.e(TAG, exception.message, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}