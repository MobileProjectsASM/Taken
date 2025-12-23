package com.asm.taken.model

import android.net.Uri
import com.asm.domain.entities.AuthUser
import com.asm.domain.errors.GeneralError

data class LoginFormUiState(
    val emailUiState: InputUiState<String, InputEmailError>,
    val passwordUiState: InputUiState<String, InputPasswordError>
)

data class InputUiState<out Value, out InputError>(
    val value: Value,
    val state: InputState<InputError> = InputState.Init
)

sealed class InputState<out InputError> {
    data object Init: InputState<Nothing>()
    data object Success: InputState<Nothing>()
    data class Error<out InputError>(
        val errors: List<InputError>
    ): InputState<InputError>()
}

sealed class CountriesUiState {
    data object Loading: CountriesUiState()
    data class Successful(val countriesInfo: List<Country>) : CountriesUiState()
    data class Failure(val generalFailure: GeneralError) : CountriesUiState()
}

data class Country(
    val name: String,
    val phoneCode: String,
    val flag: String
)

data class LoginFormPhoneUiState(
    val phoneCodeUiState: InputUiState<String, InputPhoneCodeError>,
    val phoneNumberUiState: InputUiState<String, InputPhoneNumberError>
)

sealed class LoginUiState {
    data object Loading: LoginUiState()
    data class SentOtp(val verificationId: String, val phoneNumber: String): LoginUiState()
    data class RegisteredUser(val gamerId: String): LoginUiState()
    data class UnregisteredUser(val authUser: AuthUser): LoginUiState()
    data object AccountCreated: LoginUiState()
    data object Logout: LoginUiState()
    data class Error(val generalError: GeneralError): LoginUiState()
}

data class LoginFormCreateAccountUiState(
    val emailUiState: InputUiState<String, InputEmailError>,
    val passwordUiState: InputUiState<String, InputPasswordError>,
    val passwordRepeatUiState: InputUiState<String, InputRepeatValueError>
)

data class EditGamerFormUiState(
    val imageSelected: InputUiState<ImageSelected, InputImageError>,
    val aliasUiState: InputUiState<String, InputAliasError>,
    val ageUiState: InputUiState<String, InputAgeError>,
    val countryUiState: InputUiState<CountryData, InputCountryError>
)

sealed class ImageSelected {
    data object Default: ImageSelected()
    data class NetworkImage(val urlImage: String): ImageSelected()
    data class Gallery(val uri: Uri): ImageSelected()
}

data class CountryData(
    val name: String,
    val flag: String?
)

//region ERRORS

enum class InputPasswordError {
    EMPTY,
    LEAST_THAN_8_CHARACTERS,
    LEAST_ONE_NUMBER,
    LEAST_ONE_SPECIAL_CHARACTER,
    LEAST_ONE_UPPERCASE,
}

enum class InputRepeatValueError {
    IS_NOT_SAME_VALUE
}

enum class InputPhoneCodeError {
    EMPTY,
    LESS_THAN_4_DIGITS,
    ONLY_INT_NUMBERS
}

enum class InputPhoneNumberError {
    EMPTY,
    ONLY_INT_NUMBERS
}

enum class InputEmailError {
    EMPTY,
    EMAIL_INVALID
}

enum class InputAliasError {
    EMPTY
}

enum class InputAgeError {
    EMPTY,
    ONLY_NUMBERS,
    GREATER_THAN_100,
    LESS_THAN_8
}

enum class InputCountryError {
    EMPTY
}

enum class InputOtpError {
    EMPTY,
    BE_6_DIGITS,
    ONLY_INT_NUMBERS
}

enum class InputImageError {
    IMAGE_IS_VERY_WEIGHT,
    UNKNOWN_ERROR
}
//endregion