package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result

interface CountryInfoRepository {
    suspend fun getCountriesInfo(): Result<List<CountryInfo>>
    suspend fun downloadCountriesInfo(): Result<List<CountryInfo>>
}