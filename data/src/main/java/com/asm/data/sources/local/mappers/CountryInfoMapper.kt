package com.asm.data.sources.local.mappers

import com.asm.data.sources.local.entities.CountryInfoRoom
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoMapper @Inject constructor() {
    fun getCountryInfo(countryInfoRoom: CountryInfoRoom): CountryInfo = CountryInfo(
        code = countryInfoRoom.phoneCode,
        name = countryInfoRoom.countryName,
        iso3 = countryInfoRoom.iso3,
        flag = countryInfoRoom.flag
    )

    fun getCountryInfoRoom(countryInfo: CountryInfo): CountryInfoRoom = CountryInfoRoom(
        phoneCode = countryInfo.code,
        countryName = countryInfo.name,
        iso3 = countryInfo.iso3,
        flag = countryInfo.flag
    )
}