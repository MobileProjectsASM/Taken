package com.asm.taken.model

sealed class Route(val route: String)
data object Login: Route("login")
data object SignInGamer: Route("sign_in_gamer")
data object CreateAccount: Route("create_account")
data object MainPage: Route("main_page")