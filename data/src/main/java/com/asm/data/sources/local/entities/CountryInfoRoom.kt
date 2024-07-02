package com.asm.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_info")
data class CountryInfoRoom(
    @PrimaryKey
    @ColumnInfo(name = "iso_3") val iso3: String,
    @ColumnInfo(name = "call_code") val phoneCode: String,
    @ColumnInfo(name = "country_name") val countryName: String,
    @ColumnInfo(name = "flag") val flag: String
)
