package com.asm.data.sources.remote.interfaces

import com.asm.domain.entities.CountryCallCode

interface CountryRemoteSource {
    fun getCountriesCallCode(): List<CountryCallCode>
}