package com.asm.data.sources.remote.impl.rest.mappers

import com.asm.data.sources.remote.impl.rest.data.CountryData
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoMapper @Inject constructor() {

    fun getCountryInfo(countryInfoRest: CountryData): CountryInfo {
        return CountryInfo(
            code = countryInfoRest.phoneCode ?: "",
            name = countryInfoRest.name ?: "",
            iso3 = countryInfoRest.isoCode ?: "",
            flag = countryInfoRest.flag ?: ""
        )
    }
}