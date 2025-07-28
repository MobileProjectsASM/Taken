package com.asm.taken.model

import com.asm.taken.ui.navigation.Route
import com.asm.taken.utils.UserData

sealed class InitRouteUiState {
    data object Loading: InitRouteUiState()
    data class Success(val initRoute: Route): InitRouteUiState()
    data class Fail(val error: SessionError): InitRouteUiState()
}

sealed class CloseSessionUiState {
    data object Loading: CloseSessionUiState()
    data object Logout: CloseSessionUiState()
    data class Fail(val error: SessionError): CloseSessionUiState()
}

enum class SessionError {
    SERVER_ERROR,
    NETWORK_CONNECTION,
    UNKNOWN
}