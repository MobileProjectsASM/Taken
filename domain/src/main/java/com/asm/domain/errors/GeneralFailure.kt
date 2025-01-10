package com.asm.domain.errors

sealed class GeneralFailure {
    data class ServerError(
        val code: Int,
        val description: String
    ) : GeneralFailure()
    data class OtherError(
        val errorType: GeneralErrorType
    ) : GeneralFailure()
}

enum class GeneralErrorType {
    NETWORK_CONNECTION,
    UNKNOWN
}

sealed class RegisterGeneralFailure: GeneralFailure() {
    data object GamerExists: RegisterGeneralFailure()
}

sealed class GamerGeneralFailure: GeneralFailure() {
    data object GamerNotExists: GamerGeneralFailure()
}

sealed class GameGeneralFailure: GeneralFailure() {
    data object ThereIsNotGameInProcess: GameGeneralFailure()
    data object ThereIsGameInProcess: GameGeneralFailure()
    data object MoreThanOneNewGame: GameGeneralFailure()
    data object MoreThanOneLockGame: GameGeneralFailure()
}

sealed class TimerGeneralFailure: GeneralFailure() {
    data object TimeInitIsNull: TimerGeneralFailure()
}