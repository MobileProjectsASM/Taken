package com.asm.taken.model

import com.asm.domain.errors.GeneralError
import com.asm.taken.ui.navigation.Route

sealed class InitRouteUiState {
    data object Loading: InitRouteUiState()
    data class Success(val initRoute: Route): InitRouteUiState()
    data class Fail(val error: GeneralError): InitRouteUiState()
}