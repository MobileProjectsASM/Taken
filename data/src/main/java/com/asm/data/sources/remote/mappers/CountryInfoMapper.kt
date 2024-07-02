package com.asm.data.sources.remote.mappers

import com.asm.data.sources.remote.model.CountryInfoRest
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoMapper @Inject constructor() {
    companion object {
        private const val PHONE_CODE_EXTENDED_PATTERN = "[0-9]-[0-9]{3}"
    }

    fun getCountryInfo(countryInfoRest: CountryInfoRest): CountryInfo {
        val matcher = Regex(PHONE_CODE_EXTENDED_PATTERN)
        val code = matcher.findAll(countryInfoRest.phoneCode).firstOrNull()?.value?.let {
            it.split("-")[0]
        }
        return CountryInfo(
            code = code ?: countryInfoRest.phoneCode,
            name = countryInfoRest.countryName,
            iso3 = countryInfoRest.iso3,
            flag = countryInfoRest.flag
        )
    }
}