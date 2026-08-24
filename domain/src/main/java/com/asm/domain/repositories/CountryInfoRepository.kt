package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.CommonFailure

interface CountryInfoRepository {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, CommonFailure>
    suspend fun downloadCountriesInfo(): Result<Unit, CommonFailure>
}