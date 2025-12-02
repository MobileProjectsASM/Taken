package com.asm.taken.model

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError

sealed class GamerState {
    data class Successful(
        val gamer: Gamer,
        val itHasProgress: Boolean
    ): GamerState()
    data object Loading: GamerState()
    data class Fail(val error: GeneralError): GamerState()
}

sealed class MainMenuState {
    data object Loading: MainMenuState()
    data object SessionClosed: MainMenuState()
    data class Fail(val error: GeneralError): MainMenuState()
}