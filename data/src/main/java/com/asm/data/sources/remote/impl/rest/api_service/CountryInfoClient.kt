package com.asm.data.sources.remote.impl.rest.api_service

import com.asm.data.sources.remote.model.CountryInfoRest
import retrofit2.Response
import retrofit2.http.GET

interface CountryInfoClient {
    @GET("/countries")
    suspend fun getCountriesInfo(): Response<List<CountryInfoRest>>
}