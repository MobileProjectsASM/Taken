package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.CloseSessionUC
import com.asm.domain.use_cases.CreateGamerUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.SaveSessionUC
import com.asm.taken.model.CountriesState
import com.asm.taken.model.CountryData
import com.asm.taken.model.CreateGamerProcessState
import com.asm.taken.model.CreateGamerUIState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGamerVM @Inject constructor(
    private val closeSessionUC: CloseSessionUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val createGamerUC: CreateGamerUC,
    private val saveSessionUC: SaveSessionUC
) : ViewModel() {

    companion object {
        const val TAG = "edit_gamer_view_model"
    }

    private val _createGamerUIState: MutableStateFlow<CreateGamerUIState> =
        MutableStateFlow(CreateGamerUIState())

    val createGamerUIState: StateFlow<CreateGamerUIState> = _createGamerUIState

    fun resetProcessState() {
        _createGamerUIState.update {
            it.copy(createGamerProcessState = CreateGamerProcessState.Idle)
        }
    }

    fun resetCountriesState() {
        val currentFormState = _createGamerUIState.value.createGamerFormState
        _createGamerUIState.update {
            it.copy(createGamerFormState = currentFormState.copy(countriesState = CountriesState.Idle))
        }
    }

    fun getCountriesInfo() {
        viewModelScope.launch {
            val currentFormState = _createGamerUIState.value.createGamerFormState
            _createGamerUIState.update {
                it.copy(
                    createGamerFormState = currentFormState.copy(
                        countriesState = CountriesState.Loading
                    )
                )
            }
            val countriesState: CountriesState =
                when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                    is Result.Unsuccessful -> CountriesState.Error(countriesResult.error)
                    is Result.Successful -> CountriesState.CountriesLoaded(countriesResult.data)
                }
            _createGamerUIState.update {
                it.copy(
                    createGamerFormState = currentFormState.copy(
                        countriesState = countriesState
                    )
                )
            }
        }
    }

    fun createGamer(
        id: String,
        alias: String,
        age: Int,
        country: String,
        countryFlag: String?,
        imageURI: String?
    ) {
        viewModelScope.launch {
            _createGamerUIState.update {
                it.copy(createGamerProcessState = CreateGamerProcessState.Loading)
            }
            val params = CreateGamerUC.GamerParams(
                gamerId = id,
                nickName = alias,
                age = age,
                country = country,
                countryFlag = countryFlag,
                imageURI = imageURI
            )
            val processState = when (val createGamerResult = createGamerUC.execute(params)) {
                is Result.Successful<String> -> when (val saveSessionResult =
                    saveSessionUC.execute(Session.UserRegister(gamerId = createGamerResult.data))) {
                    is Result.Successful<Unit> -> CreateGamerProcessState.GamerCreated(
                        createGamerResult.data
                    )

                    is Result.Unsuccessful<GeneralError> -> CreateGamerProcessState.Failure(
                        saveSessionResult.error
                    )
                }

                is Result.Unsuccessful<GeneralError> -> CreateGamerProcessState.Failure(
                    createGamerResult.error
                )
            }
            _createGamerUIState.update {
                it.copy(
                    createGamerProcessState = processState
                )
            }
        }
    }

    fun validateCreateGamerForm(
        alias: String, age: String, countryData: CountryData, imageURI: String?
    ) {
        val aliasErrors = validateAlias(alias)
        val ageErrors = validateAge(age)
        val countryErrors = validateCountry(countryData.name)
        val aliasInputState = aliasErrors.run {
            if (isEmpty()) InputUiState(alias, InputState.Success)
            else InputUiState(alias, InputState.Error(this))
        }
        val ageInputState = ageErrors.run {
            if (isEmpty()) InputUiState(age, InputState.Success)
            else InputUiState(age, InputState.Error(this))
        }
        val countryInputState = countryErrors.run {
            if (isEmpty()) InputUiState(countryData, InputState.Success)
            else InputUiState(countryData, InputState.Error(this))
        }

        val currentFormState = _createGamerUIState.value.createGamerFormState
        _createGamerUIState.update {
            it.copy(
                createGamerFormState = currentFormState.copy(
                    aliasUiState = aliasInputState,
                    ageUiState = ageInputState,
                    countryUiState = countryInputState,
                    imageURI = InputUiState(imageURI)
                )
            )
        }
    }

    fun closeSession() {
        viewModelScope.launch {
            _createGamerUIState.update {
                it.copy(createGamerProcessState = CreateGamerProcessState.Loading)
            }
            val processState = when (val closeSessionResult = closeSessionUC.execute(Unit)) {
                is Result.Successful<Unit> -> CreateGamerProcessState.SessionClosed
                is Result.Unsuccessful<GeneralError> -> CreateGamerProcessState.Failure(
                    closeSessionResult.error
                )
            }
            _createGamerUIState.update {
                it.copy(createGamerProcessState = processState)
            }
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
}