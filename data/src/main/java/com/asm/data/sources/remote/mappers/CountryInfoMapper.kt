package com.asm.data.sources.remote.mappers

import com.asm.data.sources.remote.model.CountryInfoRest
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoMapper @Inject constructor() {
    fun getCountryInfo(countryInfoRest: CountryInfoRest): CountryInfo = CountryInfo(
        callCode = countryInfoRest.callCode,
        name = countryInfoRest.countryName,
        iso3 = countryInfoRest.iso3,
        flag = countryInfoRest.flag
    )
}