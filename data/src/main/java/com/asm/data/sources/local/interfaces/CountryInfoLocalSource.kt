package com.asm.data.sources.local.interfaces

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure

interface CountryInfoLocalSource {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, Failure>
    suspend fun saveCountriesInfo(countriesInfo: List<CountryInfo>): Result<Unit, Failure>
}