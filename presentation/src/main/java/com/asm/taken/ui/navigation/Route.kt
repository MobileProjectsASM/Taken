package com.asm.taken.ui.navigation

import kotlinx.serialization.Serializable

sealed class Route
@Serializable
data object Login: Route()
@Serializable
data object Authentication: Route()
@Serializable
data object AuthenticationPhone: Route()
@Serializable
data object CreateAccount: Route()

@Serializable
data class CreateGamer(
    val id: String,
    val image: String? = null
): Route()

@Serializable
data object Home: Route()

@Serializable
data class MainPage(
    val gamerId: String
): Route()

@Serializable
data class EditGamer(
    val gamerId: String
): Route()