package com.asm.data.sources.remote.impl.firebase.data

object GamerKeys {
    const val GAMER_ID = "gamerId"
    const val GAMER_IMAGE = "gamerImage"
}

data class GamerFirebase(
    val gamerId: String = "",
    val gamerNickName: String = "",
    val gamerAge: Int = 0,
    val gamerCountry: String = "",
    val gamerImage: String = ""
)
