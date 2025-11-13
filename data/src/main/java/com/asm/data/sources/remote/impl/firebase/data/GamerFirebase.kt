package com.asm.data.sources.remote.impl.firebase.data

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

object GamerKeys {
    const val GAMER_ID = "gamer_id"
    const val GAMER_NICK_NAME = "gamer_nick_name"
    const val GAMER_AGE = "gamer_age"
    const val GAMER_COUNTRY = "gamer_country"
    const val GAMER_IMAGE = "gamer_image"
}

data class GamerFirebase(
    @get:PropertyName(GamerKeys.GAMER_ID)
    @set:PropertyName(GamerKeys.GAMER_ID)
    @SerializedName(GamerKeys.GAMER_ID)
    var gamerId: String = "",
    @get:PropertyName(GamerKeys.GAMER_NICK_NAME)
    @set:PropertyName(GamerKeys.GAMER_NICK_NAME)
    @SerializedName(GamerKeys.GAMER_NICK_NAME)
    var gamerNickName: String = "",
    @get:PropertyName(GamerKeys.GAMER_AGE)
    @set:PropertyName(GamerKeys.GAMER_AGE)
    @SerializedName(GamerKeys.GAMER_AGE)
    var gamerAge: Int = 0,
    @get:PropertyName(GamerKeys.GAMER_COUNTRY)
    @set:PropertyName(GamerKeys.GAMER_COUNTRY)
    @SerializedName(GamerKeys.GAMER_COUNTRY)
    var gamerCountry: String = "",
    @get:PropertyName(GamerKeys.GAMER_IMAGE)
    @set:PropertyName(GamerKeys.GAMER_IMAGE)
    @SerializedName(GamerKeys.GAMER_IMAGE)
    var gamerImage: String = ""
)
