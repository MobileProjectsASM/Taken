package com.asm.taken.vm

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.CloseSessionUC
import com.asm.domain.use_cases.CreateGamerUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.SaveSessionUC
import com.asm.taken.mappers.CountryMapper
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryData
import com.asm.taken.model.ImageSelected
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.EditGamerFormUiState
import com.asm.taken.model.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class CreateGamerVM @Inject constructor(
    private val closeSessionUC: CloseSessionUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val createGamerUC: CreateGamerUC,
    private val saveSessionUC: SaveSessionUC,
    private val countryMapper: CountryMapper,
    private val application: Application
) : ViewModel() {

    companion object {
        const val TAG = "EditGamerVM"
    }

    private val _countriesUiState: MutableStateFlow<CountriesUiState?> =
        MutableStateFlow(null)
    private val _editGamerFormUiState: MutableStateFlow<EditGamerFormUiState> =
        MutableStateFlow(
            EditGamerFormUiState(
                imageSelected = InputUiState(ImageSelected.Default),
                aliasUiState = InputUiState(""),
                ageUiState = InputUiState(""),
                countryUiState = InputUiState(CountryData("", null))
            )
        )
    private val _navigationState: MutableStateFlow<NavigationState?> = MutableStateFlow(null)

    val countriesUiState: StateFlow<CountriesUiState?> = _countriesUiState
    val editGamerFormState: StateFlow<EditGamerFormUiState> =
        _editGamerFormUiState
    val navigationState: StateFlow<NavigationState?> = _navigationState

    fun getCountriesInfo() {
        viewModelScope.launch {
            _countriesUiState.update { CountriesUiState.Loading }
            val countriesState: CountriesUiState =
                when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                    is Result.Unsuccessful -> CountriesUiState.Failure(countriesResult.error)
                    is Result.Successful -> {
                        val countries = countriesResult.data.map(countryMapper::toCountryUiState)
                        CountriesUiState.Successful(countries)
                    }
                }
            _countriesUiState.update { countriesState }
        }
    }

    fun resetNavigationState() {
        _navigationState.update { null }
    }

    fun resetCountriesState() {
        _navigationState.update { null }
    }

    //region createGamer

    fun createGamer(
        id: String,
        alias: String,
        age: Int,
        country: String,
        countryFlag: String?,
        imageSelected: ImageSelected
    ) {
        viewModelScope.launch {
            _navigationState.update { NavigationState.Loading }
            try {
                val image = when (imageSelected) {
                    ImageSelected.Default -> null
                    is ImageSelected.Gallery -> {
                        val bytes = getByteArrayFromUri(imageSelected.uri)
                        val mimeType = application.contentResolver.getType(imageSelected.uri)
                        if (bytes == null || mimeType == null) {
                            _navigationState.update { NavigationState.Failure(GeneralError.Unknown) }
                            return@launch
                        }
                        CreateGamerUC.ProfileImage.InfoImage(mimeType, bytes)
                    }

                    is ImageSelected.NetworkImage -> CreateGamerUC.ProfileImage.UrlImage(
                        imageSelected.urlImage
                    )
                }
                val params = CreateGamerUC.GamerParams(
                    gamerId = id,
                    nickName = alias,
                    age = age,
                    country = country,
                    countryFlag = countryFlag,
                    image = image
                )
                val navigationState = when (val createGamerResult = createGamerUC.execute(params)) {
                    is Result.Successful<String> -> when (val saveSessionResult =
                        saveSessionUC.execute(Session.UserRegister(gamerId = createGamerResult.data))) {
                        is Result.Successful<Unit> -> NavigationState.GamerCreated(createGamerResult.data)
                        is Result.Unsuccessful<GeneralError> -> NavigationState.Failure(
                            saveSessionResult.error
                        )
                    }

                    is Result.Unsuccessful<GeneralError> -> NavigationState.Failure(
                        createGamerResult.error
                    )
                }
                _navigationState.update { navigationState }
            } catch (exception: Exception) {
                Log.e(TAG, exception.stackTraceToString())
                _navigationState.update { NavigationState.Failure(GeneralError.Unknown) }
            }
        }
    }

    private fun getByteArrayFromUri(uri: Uri): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var inputStream: InputStream? = null
        return try {
            inputStream = application.contentResolver.openInputStream(uri) ?: return null
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }
            byteArrayOutputStream.toByteArray()
        } catch (exception: FileNotFoundException) {
            Log.e(TAG, exception.stackTraceToString())
            null
        } finally {
            try {
                inputStream?.close()
            } catch (exception: IOException) {
                Log.e(TAG, exception.stackTraceToString())
            }
        }
    }

    fun validateCreateGamerForm(
        alias: String,
        age: String,
        countryData: CountryData,
        imageSelected: ImageSelected
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

    //region close session

    fun closeSession(signOut: suspend () -> Result<Unit, GeneralError>) {
        viewModelScope.launch {
            _navigationState.update { NavigationState.Loading }
            when (val closeSessionResult = closeSessionUC.execute(signOut)) {
                is Result.Successful<Unit> -> _navigationState.update { NavigationState.SessionClosed }
                is Result.Unsuccessful<GeneralError> -> _navigationState.update {
                    NavigationState.Failure(closeSessionResult.error)
                }
            }
        }
    }

    //endregion
}