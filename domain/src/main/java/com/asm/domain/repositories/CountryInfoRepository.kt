package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result

interface CountryInfoRepository {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>>
    suspend fun downloadCountriesInfo(): Result<List<CountryInfo>>
}