package com.asm.taken.mappers

import com.asm.domain.entities.CountryInfo
import com.asm.taken.model.CountryUiState
import javax.inject.Inject

class CountryMapper @Inject constructor() {
    fun toCountryUiState(countryInfo: CountryInfo): CountryUiState =
        CountryUiState(
            country = countryInfo.name,
            phoneCode = countryInfo.phoneCode,
            flag = countryInfo.flag
        )
}