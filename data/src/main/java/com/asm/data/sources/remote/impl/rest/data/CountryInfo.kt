package com.asm.data.sources.remote.impl.rest.data

data class CountriesInfoRest(
    val countries: List<CountryInfoRest>
)

data class CountryInfoRest(
    val phoneCode: String,
    val countryName: String,
    val iso3: String,
    val flag: String
)

data class CountryError(
    val status: Int,
    val message: String
)