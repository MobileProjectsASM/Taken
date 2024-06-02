package com.asm.domain.errors

sealed class Error {
    data object NetworkConnection : Error()
    data object ServerError : Error()
    data object UnknownError : Error()

    abstract class FeatureError : Error()
}

sealed class RegisterError: Error() {
    data object GamerExists: RegisterError()
}

sealed class GamerError: Error() {
    data object GamerNotExists: GamerError()
}

sealed class GameError: Error() {
    data object ThereIsNotGameInProcess: GameError()
    data object ThereIsGameInProcess: GameError()
    data object MoreThanOneNewGame: GameError()
    data object MoreThanOneLockGame: GameError()
}

sealed class TimerError: Error() {
    data object TimeInitIsNull: TimerError()
}