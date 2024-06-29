package com.asm.domain.repositories

import com.asm.domain.entities.CountryCallCode
import com.asm.domain.entities.Result

interface CountryRepository {
    suspend fun getCountriesCallCode(): Result<List<CountryCallCode>>
    suspend fun downloadCountriesCallCode(): Result<List<CountryCallCode>>
}