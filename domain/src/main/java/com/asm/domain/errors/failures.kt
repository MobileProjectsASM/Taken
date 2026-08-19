package com.asm.domain.errors

sealed interface Failure {
    enum class SystemFailure: Failure { NETWORK_CONNECTION }
    enum class AuthenticationFailure: Failure { INVALID_CREDENTIALS }
    enum class RepositoryFailure: Failure { SERVICE_FAILURE }
    data object UnexpectedFailure: Failure
}