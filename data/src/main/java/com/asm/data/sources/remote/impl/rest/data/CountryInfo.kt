package com.asm.data.sources.remote.impl.rest.data

import com.google.gson.annotations.SerializedName

data class CountryData(
    @SerializedName("name") var name: String? = null,
    @SerializedName("isoCode") var isoCode: String? = null,
    @SerializedName("flag") var flag: String? = null,
    @SerializedName("phonecode") var phoneCode: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("timezones") var timezones: ArrayList<Timezones> = arrayListOf()
)

data class Timezones(
    @SerializedName("zoneName") var zoneName: String? = null,
    @SerializedName("gmtOffset") var gmtOffset: Int? = null,
    @SerializedName("gmtOffsetName") var gmtOffsetName: String? = null,
    @SerializedName("abbreviation") var abbreviation: String? = null,
    @SerializedName("tzName") var tzName: String? = null
)