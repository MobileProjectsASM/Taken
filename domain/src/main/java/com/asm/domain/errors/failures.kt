package com.asm.domain.errors

sealed interface AuthenticationFailure

data object InvalidCredentials: AuthenticationFailure

enum class CommonFailure: AuthenticationFailure {
    NETWORK_CONNECTION,
    REPOSITORY_FAILURE,
    UNEXPECTED_FAILURE
}
