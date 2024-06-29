package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.CountryCallCode

interface CountryLocalSource {
    fun getCountriesCallCode(): List<CountryCallCode>
    fun saveCountriesCallCode(countriesCallCode: List<CountryCallCode>)
}