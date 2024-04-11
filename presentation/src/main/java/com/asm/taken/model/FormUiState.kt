package com.asm.taken.model

data class FormUiState(
    val userIdUiState: UserIdUiState = UserIdUiState.Init,
    val passwordUiState: PasswordUiState = PasswordUiState.Init,
)

sealed class UserIdUiState(val value: String?) {
    data object Init: UserIdUiState(null)
    data object IsEmpty: UserIdUiState("")
    data class IsValid(val userId: String): UserIdUiState(userId)
}

sealed class PasswordUiState(val value: String?) {
    data object Init: PasswordUiState(null)
    data object IsEmpty: PasswordUiState("")
    data class IsInvalid(val password: String): PasswordUiState(password)
    data class IsValid(val password: String): PasswordUiState(password)
}
