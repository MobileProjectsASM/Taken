package com.asm.taken.model

data class FormUiState(
    val userIdUiState: UserIdUiState = UserIdUiState.Init,
    val passwordUiState: PasswordUiState = PasswordUiState.Init,
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

sealed class SignInState {
    data class RegisteredUser(val userId: String) : SignInState()
    data class UnregisteredUser(val userId: String) : SignInState()
    data class SignInFail(val signInError: SignInError) : SignInState()
}

enum class SignInError {
    AUTH_ERROR, REGISTER_ERROR
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)