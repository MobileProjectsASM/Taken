package com.asm.taken.mappers

import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class PhoneCodeMapper @Inject constructor() {
    fun getPhoneCode(countryInfo: CountryInfo): com.asm.taken.model.CountryInfoState =
        com.asm.taken.model.CountryInfoState(
            country = countryInfo.name,
            phoneCode = countryInfo.code,
            flag = countryInfo.flag
        )
}