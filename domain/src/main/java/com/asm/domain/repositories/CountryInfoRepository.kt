package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure

interface CountryInfoRepository {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, GeneralFailure>
    suspend fun downloadCountriesInfo(): Result<Unit, GeneralFailure>
}