package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.CountryInfo

interface CountryInfoLocalSource {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): List<CountryInfo>
    suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>)
}