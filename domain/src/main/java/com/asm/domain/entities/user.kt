package com.asm.domain.entities

data class Gamer(
    val gamerId: String,
    val gamerNickName: String,
    val gamerAge:Int,
    val gamerCountry: String,
    val gamerCountryFlag: String?,
    val gamerImage: String
)

data class AuthUser(
    val userId: String,
    val profilePictureUrl: String?
)

enum class ProviderId {
    FACEBOOK,
    GOOGLE
}