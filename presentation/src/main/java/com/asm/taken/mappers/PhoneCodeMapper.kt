package com.asm.taken.mappers

import com.asm.domain.entities.CountryInfo
import com.asm.taken.model.InfoPhoneCode
import javax.inject.Inject

class PhoneCodeMapper @Inject constructor() {
    fun getPhoneCode(countryInfo: CountryInfo): InfoPhoneCode = InfoPhoneCode(
        country = countryInfo.name,
        phoneCode = countryInfo.code,
        flag = countryInfo.flag
    )
}