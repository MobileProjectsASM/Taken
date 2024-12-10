package com.asm.taken.ui.navigation

sealed class Route(val route: String)
data object Login: Route("login")
data object Authentication: Route("authentication")
data object AuthenticationPhone: Route("authentication_phone")
data object SignInGamer: Route("sign_in_gamer")
data object CreateAccount: Route("create_account")
data object Home: Route("home")
data object MainPage: Route("main_page")