package com.asm.taken.model

import com.asm.domain.errors.GeneralFailure
import com.asm.taken.utils.UserData

sealed class SessionUiState {
    data object Loading: SessionUiState()
    data object Logout: SessionUiState()
    data class UnregisterUser(val userData: UserData): SessionUiState()
    data class UserRegister(val gamerId: String): SessionUiState()
    data class Fail(val failure: GeneralFailure): SessionUiState()
}