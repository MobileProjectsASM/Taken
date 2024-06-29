package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.CountryInfo

interface CountryInfoLocalSource {
    suspend fun getCountriesInfo(): List<CountryInfo>
    suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>)
}