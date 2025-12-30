package com.asm.taken.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.DeleteGamerUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.GetDefaultImageUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.domain.use_cases.SaveChangesGamerUC
import com.asm.taken.model.Country
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerFormUiState
import com.asm.taken.model.EditGamerOperationsState
import com.asm.taken.model.EditGamerState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGamerVM @Inject constructor(
    private val getGamerUC: GetGamerUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val getDefaultImageUC: GetDefaultImageUC,
    private val saveChangesGamerUC: SaveChangesGamerUC,
    private val deleteGamerUC: DeleteGamerUC
) : ViewModel() {

    companion object {
        const val TAG = "EditGamerVM"
    }

    private val _gamerState: MutableStateFlow<EditGamerState> =
        MutableStateFlow(EditGamerState.Loading)
    private val _editGamerFormUiState: MutableStateFlow<EditGamerFormUiState> =
        MutableStateFlow(
            EditGamerFormUiState(
                imageURI = InputUiState(null),
                aliasUiState = InputUiState(""),
                ageUiState = InputUiState(""),
                countryUiState = InputUiState(CountryData("", null))
            )
        )
    private val _editGamerOperationsState: MutableStateFlow<EditGamerOperationsState?> =
        MutableStateFlow(null)

    val gamerState: StateFlow<EditGamerState> = _gamerState
    val editGamerFormState: StateFlow<EditGamerFormUiState> =
        _editGamerFormUiState
    val editGamerOperationsState: StateFlow<EditGamerOperationsState?> = _editGamerOperationsState

    fun saveGamer(
        id: String,
        alias: String,
        age: Int,
        country: String,
        countryFlag: String?,
        imageURI: String?
    ) {
        viewModelScope.launch {
            _editGamerOperationsState.update { EditGamerOperationsState.Loading }
            try {
                val params = SaveChangesGamerUC.GamerParams(
                    gamerId = id,
                    nickName = alias,
                    age = age,
                    country = country,
                    countryFlag = countryFlag,
                    imageURI = imageURI
                )
                val navigationState =
                    when (val gamerUpdatedResult = saveChangesGamerUC.execute(params)) {
                        is Result.Successful<String> -> EditGamerOperationsState.GamerUpdated
                        is Result.Unsuccessful<GeneralError> -> EditGamerOperationsState.Failure(
                            gamerUpdatedResult.error
                        )
                    }
                _editGamerOperationsState.update { navigationState }
            } catch (exception: Exception) {
                Log.e(CreateGamerVM.TAG, exception.stackTraceToString())
                _editGamerOperationsState.update { EditGamerOperationsState.Failure(GeneralError.Unknown) }
            }
        }
    }

    fun deleteGamer(
        gamerId: String,
        signOutThirdProvider: suspend () -> Result<Unit, GeneralError>
    ) {
        viewModelScope.launch {
            _editGamerOperationsState.update { EditGamerOperationsState.Loading }
            val deleteGamerResult = deleteGamerUC.execute(
                DeleteGamerUC.DeleteGamerParams(
                    gamerId = gamerId,
                    signOutThirdProvider = signOutThirdProvider
                )
            )
            val operationState = when (deleteGamerResult) {
                is Result.Successful<Unit> -> EditGamerOperationsState.GamerDeleted
                is Result.Unsuccessful<GeneralError> -> EditGamerOperationsState.Failure(
                    deleteGamerResult.error
                )
            }
            _editGamerOperationsState.update { operationState }
        }
    }


    fun getGamerData(
        gamerId: String,
        getCurrentUserSocialNetworkImage: () -> Result<String?, GeneralError>
    ) {
        viewModelScope.launch {
            _gamerState.update { EditGamerState.Loading }
            try {

                val deferredGamerResult = async { getGamerUC.execute(gamerId) }
                val deferredCountriesInfoResult = async { getCountriesInfoUC.execute(Unit) }
                val deferredDefaultImageResult = async { getDefaultImageUC.execute(Unit) }
                val socialNetworkResult = getCurrentUserSocialNetworkImage()

                val gamerResult = deferredGamerResult.await()
                val countriesResult = deferredCountriesInfoResult.await()
                val defaultImageResult = deferredDefaultImageResult.await()
                val gamerState = when {
                    gamerResult is Result.Unsuccessful -> EditGamerState.Failure(gamerResult.error)
                    socialNetworkResult is Result.Unsuccessful -> EditGamerState.Failure(
                        socialNetworkResult.error
                    )

                    countriesResult is Result.Unsuccessful -> EditGamerState.Failure(
                        countriesResult.error
                    )

                    defaultImageResult is Result.Unsuccessful -> EditGamerState.Failure(
                        defaultImageResult.error
                    )

                    else -> {
                        val gamer = gamerResult.asSuccessful().data
                        if (gamer != null) EditGamerState.Success(
                            gamer = gamer,
                            socialNetworkImage = socialNetworkResult.asSuccessful().data,
                            defaultImageUrl = defaultImageResult.asSuccessful().data,
                            countries = countriesResult.asSuccessful().data.map {
                                Country(
                                    name = it.name,
                                    phoneCode = it.phoneCode,
                                    flag = it.flag
                                )
                            }
                        )
                        else EditGamerState.Failure(GeneralError.Unknown).also {
                            Log.e(TAG, "Gamer not found")
                        }
                    }
                }
                _gamerState.update { gamerState }
            } catch (exception: Exception) {
                Log.e(TAG, "error to get gamer data", exception)
                _gamerState.update { EditGamerState.Failure(GeneralError.Unknown) }
            }
        }
    }


    fun validateEditGamerForm(
        alias: String,
        age: String,
        countryData: CountryData,
        imageURI: String?
    ) {
        val aliasErrors = validateAlias(alias)
        val ageErrors = validateAge(age)
        val countryErrors = validateCountry(countryData.name)
        _editGamerFormUiState.update {
            EditGamerFormUiState(
                aliasUiState = aliasErrors.run {
                    if (isEmpty()) InputUiState(alias, InputState.Success)
                    else InputUiState(alias, InputState.Error(this))
                },
                ageUiState = ageErrors.run {
                    if (isEmpty()) InputUiState(age, InputState.Success)
                    else InputUiState(age, InputState.Error(this))
                },
                countryUiState = countryErrors.run {
                    if (isEmpty()) InputUiState(countryData, InputState.Success)
                    else InputUiState(countryData, InputState.Error(this))
                },
                imageURI = InputUiState(imageURI)
            )
        }
    }

    fun resetEditGamerOperationsState() {
        _editGamerOperationsState.update { null }
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