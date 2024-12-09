package com.asm.taken.mappers

import com.asm.domain.entities.CountryInfo
import com.asm.taken.model.CountryUiState
import javax.inject.Inject

class PhoneCodeMapper @Inject constructor() {
    fun getPhoneCode(countryInfo: CountryInfo): CountryUiState =
        CountryUiState(
            country = countryInfo.name,
            phoneCode = countryInfo.code,
            flag = countryInfo.flag
        )
}