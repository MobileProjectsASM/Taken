package com.asm.data.sources.remote.interfaces

import com.asm.domain.entities.CountryInfo

interface CountryInfoRemoteSource {
    suspend fun getCountriesCallCode(): List<CountryInfo>
}