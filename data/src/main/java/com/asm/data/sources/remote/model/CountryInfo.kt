package com.asm.data.sources.remote.model

data class CountryInfoRest(
    val callCode: String,
    val countryName: String,
    val iso3: String,
    val flag: String
)

data class CountryError(
    val status: String,
    val message: String
)