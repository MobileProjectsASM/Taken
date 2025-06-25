package com.asm.domain.entities

sealed class Session {
    data class UserUnregister(
        val userId: String,
        val userImage: String?
    ): Session()
    data class UserRegister(val gamerId: String): Session()
}