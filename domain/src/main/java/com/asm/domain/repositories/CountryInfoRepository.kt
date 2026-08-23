package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure

interface CountryInfoRepository {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, Failure>
    suspend fun downloadCountriesInfo(): Result<Unit, Failure>
}