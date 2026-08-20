package com.asm.taken.model

import androidx.collection.ObjectList
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GeneralError

data class InputUiState<out Value, out InputError>(
    val value: Value,
    val state: InputState<InputError> = InputState.Idle
)

sealed class InputState<out InputError> {
    data object Idle : InputState<Nothing>()
    data object Success : InputState<Nothing>()
    data class Error<out InputError>(
        val errors: List<InputError>
    ) : InputState<InputError>()
}

data class PhoneAuthFormState(
    val phoneCodeUiState: InputUiState<String, InputPhoneCodeError> = InputUiState(""),
    val phoneNumberUiState: InputUiState<String, InputPhoneNumberError> = InputUiState(""),
    val dataFormProcess: CountriesState = CountriesState.Idle
)

data class OtpFormState(
    val otpInputState: InputUiState<String, InputOtpError> = InputUiState("")
)

data class PhoneAuthUIState(
    val phoneAuthFormState: PhoneAuthFormState = PhoneAuthFormState(),
    val phoneAuthProcessState: AuthPhoneProcessState = AuthPhoneProcessState.Idle
)

sealed class CountriesState {
    data object Idle : CountriesState()
    data object Loading : CountriesState()
    data class CountriesLoaded(val countriesInfo: List<CountryInfo>) : CountriesState()
    data class Error(val generalError: GeneralError) : CountriesState()
}

sealed class AuthPhoneProcessState {
    data object Idle : AuthPhoneProcessState()
    data object Loading : AuthPhoneProcessState()
    data class SentOtp(
        val verificationId: String,
        val phoneNumber: String,
        val otpFormState: OtpFormState = OtpFormState()
    ) : AuthPhoneProcessState()

    data class RegisteredUser(val gamerId: String) : AuthPhoneProcessState()
    data class UnregisteredUser(val authUser: AuthUser) : AuthPhoneProcessState()
    data class Error(val failure: Failure) : AuthPhoneProcessState()
}

data class EmailAndPasswordFormState(
    val emailUiState: InputUiState<String, InputEmailError> = InputUiState(""),
    val passwordUiState: InputUiState<String, InputPasswordError> = InputUiState("")
)

data class LoginUIState(
    val emailAndPasswordFormState: EmailAndPasswordFormState = EmailAndPasswordFormState(),
    val authTypeState: AuthTypeState = AuthTypeState.Idle
)

sealed class AuthTypeState {
    data object Idle : AuthTypeState()
    data class EmailAndPasswordAuthType(val authState: AuthState) : AuthTypeState()
    data class GoogleAuthType(val authState: AuthState) : AuthTypeState()
    data class FacebookAuthType(val authState: AuthState) : AuthTypeState()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class RegisteredUser(val gamerId: String) : AuthState()
    data class UnregisteredUser(val authUser: AuthUser) : AuthState()
    data class Error(val failure: Failure) : AuthState()
}

data class CreateAccountUIState(
    val createAccountFormState: CreateAccountFormState = CreateAccountFormState(),
    val createAccountProcessState: CreateAccountProcessState = CreateAccountProcessState.Idle
)

sealed class CreateAccountProcessState {
    data object Idle : CreateAccountProcessState()
    data object Loading : CreateAccountProcessState()
    data object AccountProcessCreated : CreateAccountProcessState()
    data class Error(val generalError: GeneralError) : CreateAccountProcessState()
}

data class CreateAccountFormState(
    val emailUiState: InputUiState<String, InputEmailError> = InputUiState(""),
    val passwordUiState: InputUiState<String, InputPasswordError> = InputUiState(""),
    val passwordRepeatUiState: InputUiState<String, InputRepeatValueError> = InputUiState("")
)

data class CreateGamerUIState(
    val createGamerFormState: CreateGamerFormState = CreateGamerFormState(),
    val createGamerProcessState: CreateGamerProcessState = CreateGamerProcessState.Idle
)

data class CreateGamerFormState(
    val imageURI: InputUiState<String?, InputImageError> = InputUiState(null),
    val aliasUiState: InputUiState<String, InputAliasError> = InputUiState(""),
    val ageUiState: InputUiState<String, InputAgeError> = InputUiState(""),
    val countryUiState: InputUiState<CountryData, InputCountryError> = InputUiState(CountryData()),
    val countriesState: CountriesState = CountriesState.Idle
)

sealed class CreateGamerProcessState {
    data object Idle: CreateGamerProcessState()
    data object Loading: CreateGamerProcessState()
    data object SessionClosed: CreateGamerProcessState()
    data class GamerCreated(val gamerId: String): CreateGamerProcessState()
    data class Failure(val error: GeneralError): CreateGamerProcessState()
}

data class CountryData(
    val name: String = "",
    val flag: String? = null
)


data class MetaDataEditForm(
    val imageURI: InputUiState<String?, InputImageError> = InputUiState(null),
    val aliasUiState: InputUiState<String, InputAliasError> = InputUiState(""),
    val ageUiState: InputUiState<String, InputAgeError> = InputUiState(""),
    val countryUiState: InputUiState<CountryData, InputCountryError> = InputUiState(CountryData()),
    val countries: List<CountryInfo>,
    val gamer: Gamer,
    val defaultImageUrl: String?,
    val socialNetworkImage: String?,
    val editGamerProcessType: EditGamerProcessType = EditGamerProcessType.Idle
)

sealed class EditGamerUIState {
    data object Loading : EditGamerUIState()
    data class Success(val metaDataEditForm: MetaDataEditForm): EditGamerUIState()
    data class Failure(val error: GeneralError): EditGamerUIState()
}

sealed class EditGamerProcessType {
    data object Idle: EditGamerProcessType()
    data class DeleteGamerState(val processState: CommonProcessState<Unit>): EditGamerProcessType()
    data class UpdateGamerState(val processState: CommonProcessState<Unit>): EditGamerProcessType()
}

sealed class CommonProcessState<out Data> {
    data object Idle : CommonProcessState<Nothing>()
    data object Loading: CommonProcessState<Nothing>()
    data class Success<out Data>(val data: Data): CommonProcessState<Data>()
    data class Failure(val error: GeneralError): CommonProcessState<Nothing>()
}

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