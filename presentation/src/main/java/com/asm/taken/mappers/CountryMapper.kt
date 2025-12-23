package com.asm.taken.mappers

import com.asm.domain.entities.CountryInfo
import com.asm.taken.model.Country
import javax.inject.Inject

class CountryMapper @Inject constructor() {
    fun toCountryUiState(countryInfo: CountryInfo): Country =
        Country(
            name = countryInfo.name,
            phoneCode = countryInfo.phoneCode,
            flag = countryInfo.flag
        )
}