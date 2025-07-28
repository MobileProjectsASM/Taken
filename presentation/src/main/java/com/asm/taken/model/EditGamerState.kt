package com.asm.taken.model

import com.asm.domain.errors.GeneralFailure

sealed class NavigationState {
    data object Loading: NavigationState()
    data object SessionClosed: NavigationState()
    data class GamerCreated(val gamerId: String): NavigationState()
    data class Failure(val failure: GeneralFailure): NavigationState()
}

