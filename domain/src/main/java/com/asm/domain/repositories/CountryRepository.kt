package com.asm.domain.repositories

import com.asm.domain.entities.CountryCallCode
import com.asm.domain.entities.Result

interface CountryRepository {
    fun getCountriesCallCode(): Result<List<CountryCallCode>>
}