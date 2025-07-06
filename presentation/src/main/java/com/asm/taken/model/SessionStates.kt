package com.asm.taken.model

import com.asm.taken.ui.navigation.Route
import com.asm.taken.utils.UserData

sealed class InitRouteUiState {
    data object Loading: InitRouteUiState()
    data class Success(val initRoute: Route): InitRouteUiState()
    data class Fail(val error: SessionError): InitRouteUiState()
}

sealed class SessionUiState {
    data object Loading: SessionUiState()
    data object Logout: SessionUiState()
    data class UnregisterUser(val userData: UserData): SessionUiState()
    data class UserRegister(val gamerId: String): SessionUiState()
    data class Fail(val error: SessionError): SessionUiState()
}

enum class SessionError {
    SERVER_ERROR,
    NETWORK_CONNECTION,
    UNKNOWN
}