package com.asm.data.sources.remote.impl.firebase.data

import com.google.gson.annotations.SerializedName

object GamerKeys {
    const val GAMER_ID = "gamer_id"
    const val GAMER_NICK_NAME = "gamer_nick_name"
    const val GAMER_AGE = "gamer_age"
    const val GAMER_COUNTRY = "gamer_country"
    const val GAMER_IMAGE = "gamer_image"
}

data class GamerFirebase(
    @SerializedName(GamerKeys.GAMER_ID)
    val gamerId: String,
    @SerializedName(GamerKeys.GAMER_NICK_NAME)
    val gamerNickName: String,
    @SerializedName(GamerKeys.GAMER_AGE)
    val gamerAge: Int,
    @SerializedName(GamerKeys.GAMER_COUNTRY)
    val gamerCountry: String,
    @SerializedName(GamerKeys.GAMER_IMAGE)
    val gamerImage: String
)
