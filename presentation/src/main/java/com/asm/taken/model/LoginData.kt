package com.asm.taken.model

data class LoginData(
    val userId: String? = null,
    val password: String? = null,
    val userIdMessage: String? = null,
    val passwordMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val btnLoginEnable: Boolean = false
)
