package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.taken.mappers.CountryMapper
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.ImageSelected
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.LoginCreateGamerFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGamerVM @Inject constructor(
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val countryMapper: CountryMapper
): ViewModel() {

    companion object {
        const val TAG = "EditGamerVM"
    }

    private val _countriesUiState: MutableStateFlow<CountriesUiState> = MutableStateFlow(CountriesUiState.Loading)
    private val _loginCreateGamerFormUiState: MutableStateFlow<LoginCreateGamerFormUiState> = MutableStateFlow(
        LoginCreateGamerFormUiState(imageSelected = InputUiState(ImageSelected.Default), aliasUiState = InputUiState(""), ageUiState = InputUiState(""), countryUiState = InputUiState(""))
    )

    val countriesUiState: StateFlow<CountriesUiState> = _countriesUiState
    val loginCreateGamerFormState: StateFlow<LoginCreateGamerFormUiState> = _loginCreateGamerFormUiState

    fun getCountriesInfo() {
        viewModelScope.launch {
            _countriesUiState.update { CountriesUiState.Loading }
            val countriesState: CountriesUiState = when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                is Result.Unsuccessful -> CountriesUiState.Failure(countriesResult.failure)
                is Result.Successful -> {
                    val countries = countriesResult.data.map(countryMapper::toCountryUiState)
                    CountriesUiState.Successful(countries)
                }
            }
            _countriesUiState.update { countriesState }
        }
    }

    //region createGamer

    fun createGamer(id: String, alias: String, age: Int, country: String, imageSelected: ImageSelected) {
        viewModelScope.launch {

        }
    }

    fun validateCreateGamerForm(alias: String, age: String, country: String, imageSelected: ImageSelected) {
        val aliasErrors = validateAlias(alias)
        val ageErrors = validateAge(age)
        val countryErrors = validateCountry(country)
        _loginCreateGamerFormUiState.update {
            LoginCreateGamerFormUiState(
                aliasUiState = aliasErrors.run {
                    if (isEmpty()) InputUiState(alias, InputState.Success)
                    else InputUiState(alias, InputState.Error(this))
                },
                ageUiState = ageErrors.run {
                    if (isEmpty()) InputUiState(age, InputState.Success)
                    else InputUiState(age, InputState.Error(this))
                },
                countryUiState = countryErrors.run {
                    if (isEmpty()) InputUiState(country, InputState.Success)
                    else InputUiState(country, InputState.Error(this))
                },
                imageSelected = InputUiState(imageSelected)
            )
        }
    }

    private fun validateAlias(alias: String): List<InputAliasError> {
        val aliasErrors = mutableListOf<InputAliasError>()
        if (alias.isEmpty()) aliasErrors.add(InputAliasError.EMPTY)
        return aliasErrors
    }

    private fun validateAge(age: String): List<InputAgeError> {
        val ageErrors = mutableListOf<InputAgeError>()
        if (age.isEmpty()) ageErrors.add(InputAgeError.EMPTY)
        try {
            val ageInt = age.toInt()
            if (ageInt > 100) ageErrors.add(InputAgeError.GREATER_THAN_100)
            if (ageInt < 8) ageErrors.add(InputAgeError.LESS_THAN_8)
        } catch (exception: NumberFormatException) {
            ageErrors.add(InputAgeError.ONLY_NUMBERS)
        }
        return ageErrors
    }

    private fun validateCountry(country: String): List<InputCountryError> {
        val countryErrors = mutableListOf<InputCountryError>()
        if (country.isEmpty()) countryErrors.add(InputCountryError.EMPTY)
        return countryErrors
    }

    //endregion
}