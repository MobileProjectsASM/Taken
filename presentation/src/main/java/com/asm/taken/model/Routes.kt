package com.asm.taken.model

sealed class Route(val route: String)
data object Login: Route("login")