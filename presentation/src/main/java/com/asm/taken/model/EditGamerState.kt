package com.asm.taken.model

import com.asm.domain.errors.GeneralFailure

sealed class CreateGamerState {
    data object Loading: CreateGamerState()
    data class GamerCreated(val gamerId: String): CreateGamerState()
    data class Failure(val failure: GeneralFailure): CreateGamerState()
}

