package com.asm.taken.model

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError

sealed class EditGamerState {
    data object Loading: EditGamerState()
    data class Success(
        val gamer: Gamer,
        val socialNetworkImage: String?,
        val defaultImageUrl: String?,
        val countries: List<CountryInfo>
    ): EditGamerState()
    data class Failure(val error: GeneralError): EditGamerState()
}