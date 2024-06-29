package com.asm.data.sources.local.mappers

import com.asm.data.sources.local.entities.CountryInfoRoom
import com.asm.domain.entities.CountryInfo
import javax.inject.Inject

class CountryInfoMapper @Inject constructor() {
    fun getCountryInfo(countryInfoRoom: CountryInfoRoom): CountryInfo = CountryInfo(
        countryInfoRoom.callCode,
        countryInfoRoom.countryName,
        countryInfoRoom.iso3,
        countryInfoRoom.flag
    )

    fun getCountryInfoRoom(countryInfo: CountryInfo): CountryInfoRoom = CountryInfoRoom(
        countryInfo.callCode,
        countryInfo.name,
        countryInfo.iso3,
        countryInfo.flag
    )
}