package com.asm.taken.model

import com.asm.domain.entities.AuthUser
import com.asm.domain.errors.GeneralError

data class EmailAndPasswordFormState(
    val emailUiState: InputUiState<String, InputEmailError> = InputUiState(""),
    val passwordUiState: InputUiState<String, InputPasswordError> = InputUiState("")
)

data class InputUiState<out Value, out InputError>(
    val value: Value,
    val state: InputState<InputError> = InputState.Idle
)

sealed class InputState<out InputError> {
    data object Idle: InputState<Nothing>()
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

data class LoginUIState2(
    val emailAndPasswordFormState: EmailAndPasswordFormState = EmailAndPasswordFormState(),
    val authTypeState: AuthTypeState = AuthTypeState.Idle
)

sealed class AuthTypeState {
    data object Idle: AuthTypeState()
    data class EmailAndPasswordAuthType(val authState: AuthState): AuthTypeState()
    data class GoogleAuthType(val authState: AuthState): AuthTypeState()
    data class FacebookAuthType(val authState: AuthState): AuthTypeState()
}

sealed class AuthState {
    data object Idle: AuthState()
    data object Loading: AuthState()
    data class RegisteredUser(val gamerId: String): AuthState()
    data class UnregisteredUser(val authUser: AuthUser): AuthState()
    data class Error(val generalError: GeneralError): AuthState()
}

sealed class LoginUiState {
    data object Loading: LoginUiState()
    data class SentOtp(val verificationId: String, val phoneNumber: String): LoginUiState()
    data class RegisteredUser(val gamerId: String): LoginUiState()
    data class UnregisteredUser(val authUser: AuthUser): LoginUiState()
    data object AccountCreated: LoginUiState()
    data object Logout: LoginUiState()
    data class Error(val generalError: GeneralError): LoginUiState()
}

data class CreateAccountUIState(
    val createAccountFormState: CreateAccountFormState = CreateAccountFormState(),
    val createAccountProcessState: CreateAccountProcessState = CreateAccountProcessState.Idle
)

sealed class CreateAccountProcessState {
    data object Idle: CreateAccountProcessState()
    data object Loading: CreateAccountProcessState()
    data object AccountProcessCreated: CreateAccountProcessState()
    data class Error(val generalError: GeneralError): CreateAccountProcessState()
}

data class CreateAccountFormState(
    val emailUiState: InputUiState<String, InputEmailError> = InputUiState(""),
    val passwordUiState: InputUiState<String, InputPasswordError> = InputUiState(""),
    val passwordRepeatUiState: InputUiState<String, InputRepeatValueError> = InputUiState("")
)

data class EditGamerFormUiState(
    val imageURI: InputUiState<String?, InputImageError>,
    val aliasUiState: InputUiState<String, InputAliasError>,
    val ageUiState: InputUiState<String, InputAgeError>,
    val countryUiState: InputUiState<CountryData, InputCountryError>
)

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