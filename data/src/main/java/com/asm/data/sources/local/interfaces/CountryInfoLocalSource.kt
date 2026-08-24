package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.CommonFailure

interface CountryInfoLocalSource {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, CommonFailure>
    suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>): Result<Unit, CommonFailure>
}