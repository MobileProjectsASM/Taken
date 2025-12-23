package com.asm.taken.model

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError

sealed class EditGamerState {
    data object Loading: EditGamerState()
    data class Success(
        val gamer: Gamer,
        val socialNetworkImage: String?,
        val countries: List<Country>
    ): EditGamerState()
    data class Failure(val error: GeneralError): EditGamerState()
}