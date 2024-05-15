package com.asm.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gamers")
data class Gamer(
    @PrimaryKey
    @ColumnInfo(name = "gamer_id") val gamerId: String,
    @ColumnInfo(name = "gamer_nick_name") val gamerNickname: String,
    @ColumnInfo(name = "gamer_age") val gamerAge: Int,
    @ColumnInfo(name = "gamer_country") val gamerCountry: String,
    @ColumnInfo(name = "gamer_image") val gamerImage: String,
)