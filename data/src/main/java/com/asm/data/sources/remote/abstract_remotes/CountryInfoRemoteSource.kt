package com.asm.data.sources.remote.abstract_remotes

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralFailure

interface CountryInfoRemoteSource {
    suspend fun getCountriesCallCode(): Result<List<CountryInfo>, GeneralFailure>
}