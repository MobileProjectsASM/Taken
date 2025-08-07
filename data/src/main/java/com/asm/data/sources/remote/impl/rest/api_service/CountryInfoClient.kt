package com.asm.data.sources.remote.impl.rest.api_service

import com.asm.data.sources.remote.impl.rest.data.CountryData
import retrofit2.Response
import retrofit2.http.GET

interface CountryInfoClient {
    @GET("allcountries")
    suspend fun getCountriesInfo(): Response<List<CountryData>>
}