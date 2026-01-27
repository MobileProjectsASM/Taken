package com.asm.taken.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.DeleteGamerUC
import com.asm.domain.use_cases.GetAuthUserUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.GetDefaultImageUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.domain.use_cases.SaveChangesGamerUC
import com.asm.taken.model.CommonProcessState
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerProcessType
import com.asm.taken.model.EditGamerUIState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.MetaDataEditForm
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
    private val getAuthUserUC: GetAuthUserUC,
    private val deleteGamerUC: DeleteGamerUC
) : ViewModel() {

    companion object {
        const val TAG = "EditGamerVM"
    }

    private val _editGamerUIState: MutableStateFlow<EditGamerUIState> = MutableStateFlow(
        EditGamerUIState.Loading
    )

    val editGamerUIState: StateFlow<EditGamerUIState> = _editGamerUIState

    fun resetEditGamerProcessState() {
        val currentState = _editGamerUIState.value
        if (currentState is EditGamerUIState.Success) {
            val metaData = currentState.metaDataEditForm
            _editGamerUIState.update {
                EditGamerUIState.Success(metaData.copy(editGamerProcessType = EditGamerProcessType.Idle))
            }
        }
    }

    fun saveGamer(
        id: String,
        alias: String,
        age: Int,
        country: String,
        countryFlag: String?,
        imageURI: String?
    ) {
        val editGamerState = _editGamerUIState.value
        if (editGamerState is EditGamerUIState.Success) {
            viewModelScope.launch {
                _editGamerUIState.update {
                    EditGamerUIState.Success(
                        metaDataEditForm = editGamerState.metaDataEditForm.copy(
                            editGamerProcessType = EditGamerProcessType.UpdateGamerState(
                                processState = CommonProcessState.Loading
                            )
                        )
                    )
                }
                val params = SaveChangesGamerUC.GamerParams(
                    gamerId = id,
                    nickName = alias,
                    age = age,
                    country = country,
                    countryFlag = countryFlag,
                    imageURI = imageURI
                )
                val processState =
                    when (val gamerUpdatedResult = saveChangesGamerUC.execute(params)) {
                        is Result.Successful<String> -> CommonProcessState.Success(Unit)
                        is Result.Unsuccessful<GeneralError> -> CommonProcessState.Failure(
                            gamerUpdatedResult.error
                        )
                    }
                _editGamerUIState.update {
                    EditGamerUIState.Success(
                        metaDataEditForm = editGamerState.metaDataEditForm.copy(
                            editGamerProcessType = EditGamerProcessType.UpdateGamerState(
                                processState = processState
                            )
                        )
                    )
                }
            }
        }
    }

    fun getGamerData(
        gamerId: String
    ) {
        viewModelScope.launch {
            _editGamerUIState.update { EditGamerUIState.Loading }

            val deferredGamerResult = async { getGamerUC.execute(gamerId) }
            val deferredCountriesInfoResult = async { getCountriesInfoUC.execute(Unit) }
            val deferredDefaultImageResult = async { getDefaultImageUC.execute(Unit) }
            val deferredSocialNetworkResult = async { getAuthUserUC.execute(Unit) }

            val gamerResult = deferredGamerResult.await()
            val countriesResult = deferredCountriesInfoResult.await()
            val defaultImageResult = deferredDefaultImageResult.await()
            val socialNetworkResult = deferredSocialNetworkResult.await()
            val metaDataState = when {
                gamerResult is Result.Unsuccessful -> EditGamerUIState.Failure(gamerResult.error)
                socialNetworkResult is Result.Unsuccessful -> EditGamerUIState.Failure(
                    socialNetworkResult.error
                )

                countriesResult is Result.Unsuccessful -> EditGamerUIState.Failure(countriesResult.error)

                defaultImageResult is Result.Unsuccessful -> EditGamerUIState.Failure(
                    defaultImageResult.error
                )

                else -> {
                    val gamer = gamerResult.asSuccessful().data
                    if (gamer != null) EditGamerUIState.Success(
                        metaDataEditForm = MetaDataEditForm(
                            imageURI = InputUiState(gamer.gamerImage),
                            aliasUiState = InputUiState(gamer.gamerNickName),
                            ageUiState = InputUiState(gamer.gamerAge.toString()),
                            countryUiState = InputUiState(
                                CountryData(
                                    gamer.gamerCountry,
                                    gamer.gamerCountryFlag
                                )
                            ),
                            gamer = gamer,
                            socialNetworkImage = socialNetworkResult.asSuccessful().data.profilePictureUrl,
                            defaultImageUrl = defaultImageResult.asSuccessful().data,
                            countries = countriesResult.asSuccessful().data
                        ),
                    )
                    else EditGamerUIState.Failure(GeneralError.Unknown).also {
                        Log.e(TAG, "Gamer not found")
                    }
                }
            }

            _editGamerUIState.update { metaDataState }
        }
    }


    fun validateEditGamerForm(
        alias: String, age: String, countryData: CountryData, imageURI: String?
    ) {
        val aliasErrors = validateAlias(alias)
        val ageErrors = validateAge(age)
        val countryErrors = validateCountry(countryData.name)
        val aliasInputState = aliasErrors.run {
            if (isEmpty()) InputUiState(alias, InputState.Success)
            else InputUiState(alias, InputState.Error(this))
        }
        val countryInputState = countryErrors.run {
            if (isEmpty()) InputUiState(countryData, InputState.Success)
            else InputUiState(countryData, InputState.Error(this))
        }
        val ageInputState = ageErrors.run {
            if (isEmpty()) InputUiState(age, InputState.Success)
            else InputUiState(age, InputState.Error(this))
        }

        val currentFormState = _editGamerUIState.value

        if (currentFormState is EditGamerUIState.Success) {
            val editGamerUIState = EditGamerUIState.Success(
                metaDataEditForm = currentFormState.metaDataEditForm.copy(
                    aliasUiState = aliasInputState,
                    ageUiState = ageInputState,
                    countryUiState = countryInputState,
                    imageURI = InputUiState(imageURI)
                )
            )
            _editGamerUIState.update { editGamerUIState }
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