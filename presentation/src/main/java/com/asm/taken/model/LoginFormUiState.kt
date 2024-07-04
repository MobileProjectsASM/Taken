package com.asm.taken.model

data class LoginFormUiState(
    val userIdUiState: UserIdUiState = UserIdUiState.Init,
    val passwordUiState: PasswordUiState = PasswordUiState.Init,
)

data class SendPhoneFormUiState(
    val phoneCodeState: PhoneCodeState = PhoneCodeState.Init,
    val phoneNumberState: PhoneNumberState = PhoneNumberState.Init
)

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

sealed class SignInState {
    data class RegisteredUser(val userId: String) : SignInState()
    data class UnregisteredUser(val userId: String) : SignInState()
    data class PhoneCodeSent(val verificationId: String): SignInState()
    data class SignInFail(val signInError: SignInError) : SignInState()
}

sealed class CountriesInfoState {
    data object Loading: CountriesInfoState()
    data class Successful(val countriesInfo: List<CountryInfoState>) : CountriesInfoState()
    data class Failure(val errorMessage: String) : CountriesInfoState()
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