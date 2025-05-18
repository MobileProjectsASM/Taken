package com.asm.taken.model

import com.asm.domain.errors.GeneralFailure

/*data class LoginFormState(
    val userIdUiState: UserIdUiState = UserIdUiState.Init,
    val passwordUiState: PasswordUiState = PasswordUiState.Init,
)

sealed class SendPhoneFormState {
    enum class PhoneFormData {
        COUNTRY_CODE,
        LOCAL_PHONE
    }
    enum class ErrorPhoneFormData {
        GET_COUNTRIES
    }

    data object Loading: SendPhoneFormState()
    data class ErrorForm(val errorPhoneFormData: ErrorPhoneFormData): SendPhoneFormState()
    data class BuildForm(val countries: List<CountryInfoState>): SendPhoneFormState()
    data class InvalidData(val invalidData: Map<PhoneFormData, Boolean>): SendPhoneFormState()
    data class ValidField(val code: String): SendPhoneFormState()
}



sealed class UserIdUiState(val value: String?) {
    data object Init : UserIdUiState(null)
    data object IsEmpty : UserIdUiState("")
    data class IsValid(val userId: String) : UserIdUiState(userId)
}

sealed class PasswordUiState(val value: String?) {
    data object Init : PasswordUiState(null)
    data object IsEmpty : PasswordUiState("")
    data class IsInvalid(val password: String) : PasswordUiState(password)
    data class IsValid(val password: String) : PasswordUiState(password)
}

sealed class PhoneNumberState(val value: String?) {
    data object Init: PhoneNumberState(null)
    data object IsEmpty: PhoneNumberState("")
    data class IsInvalid(val phoneNumber: String): PhoneNumberState(phoneNumber)
    data class IsValid(val phoneNumber: String): PhoneNumberState(phoneNumber)
}

sealed class PhoneCodeState(val value: String?) {
    data object Init: PhoneCodeState(null)
    data object IsEmpty: PhoneCodeState("")
    data class IsInvalid(val phoneCode: String): PhoneCodeState(phoneCode)
    data class IsValid(val phoneCode: String): PhoneCodeState(phoneCode)
}

sealed class SentCodeState(val value: String?) {
    data object Init: SentCodeState(null)
    data object IsEmpty: SentCodeState("")
    data class IsInvalid(val validateCode: String): SentCodeState(validateCode)
    data class IsValid(val validateCode: String): SentCodeState(validateCode)
}

sealed class SignInState {
    data class RegisteredUser(val userId: String) : SignInState()
    data class UnregisteredUser(val userId: String) : SignInState()
    data class PhoneCodeSent(val verificationId: String): SignInState()
    data class SignInFail(val signInError: SignInError) : SignInState()
}

sealed class GetCountriesInfoState {
    data object Loading: GetCountriesInfoState()
    data class Successful(val countriesInfo: List<CountryInfoState>) : GetCountriesInfoState()
    data class Failure(val errorMessage: String) : GetCountriesInfoState()
}

enum class SignInError {
    AUTH_ERROR, REGISTER_ERROR
}

sealed class AuthResult {
    data class Successful(val data: UserData): AuthResult()
    data class PhoneCodeSent(val verificationId: String): AuthResult()
    data class Failure(val errorMessage: String): AuthResult()
}

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)

data class CountryInfoState(
    val country: String,
    val phoneCode: String,
    val flag: String
)
 */

data class LoginFormUiState(
    val emailUiState: InputUiState<InputEmailError>,
    val passwordUiState: InputUiState<InputPasswordError>
)

data class InputUiState<out InputError>(
    val value: String = "",
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
    data class Successful(val countriesInfo: List<CountryUiState>) : CountriesUiState()
    data class Failure(val generalFailure: GeneralFailure) : CountriesUiState()
}

data class CountryUiState(
    val country: String,
    val phoneCode: String,
    val flag: String
)

data class LoginFormPhoneUiState(
    val phoneCodeUiState: InputUiState<InputPhoneCodeError>,
    val phoneNumberUiState: InputUiState<InputPhoneNumberError>
)

sealed class LoginUiState {
    data object Loading: LoginUiState()
    data class SentOtp(val verificationId: String, val phoneNumber: String): LoginUiState()
    data class RegisteredUser(val gamerId: String): LoginUiState()
    data class UnregisteredUser(val userId: String): LoginUiState()
    data object AccountCreated: LoginUiState()
    data class Failure(val loginFailure: LoginFailure): LoginUiState()
}

data class LoginFormCreateAccountUiState(
    val emailUiState: InputUiState<InputEmailError>,
    val passwordUiState: InputUiState<InputPasswordError>,
    val passwordRepeatUiState: InputUiState<InputRepeatValueError>
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

enum class SendOtpError {
    PHONE_NUMBER_INVALID_ERROR,
    NETWORK_CONNECTION,
    SERVER_ERROR,
    UNKNOWN_ERROR
}

enum class AuthError {
    AUTH_ERROR,
    NETWORK_CONNECTION,
    UNKNOWN_ERROR
}

enum class SignUpError {
    NETWORK_CONNECTION,
    EMAIL_ALREADY_IN_USE,
    INVALID_EMAIL,
    WEAK_PASSWORD,
    UNKNOWN_ERROR
}

enum class InputOtpError {
    EMPTY,
    BE_6_DIGITS,
    ONLY_INT_NUMBERS
}

sealed class LoginFailure {
    data class SendOtpFailure(val sendOtpError: SendOtpError): LoginFailure()
    data class AuthFailure(val authError: AuthError): LoginFailure()
    data class SignUpFailure(val signUpError: SignUpError): LoginFailure()
    data class RegisterFailure(val generalFailure: GeneralFailure): LoginFailure()
}
//endregion