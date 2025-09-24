package com.asm.domain.repositories

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError

interface CountryInfoRepository {
    suspend fun getCountriesInfoSortedByName(ascending: Boolean = true): Result<List<CountryInfo>, GeneralError>
    suspend fun downloadCountriesInfo(): Result<Unit, GeneralError>
}