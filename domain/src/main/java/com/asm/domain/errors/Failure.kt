package com.asm.domain.errors

import com.asm.domain.entities.Timer

sealed class Failure {
    data object NetworkConnection : Failure()
    data object ServerError : Failure()
    data object UnknownError : Failure()

    abstract class FeatureFailure : Failure()
}

sealed class RegisterFailure: Failure() {
    data object GamerExists: RegisterFailure()
}

sealed class GamerFailure: Failure() {
    data object GamerNotExists: GamerFailure()
}

sealed class GameFailure: Failure() {
    data object ThereIsNotGameInProcess: GameFailure()
    data object ThereIsGameInProcess: GameFailure()
    data object MoreThanOneNewGame: GameFailure()
    data object MoreThanOneLockGame: GameFailure()
}

sealed class TimerFailure: Failure() {
    data object TimeInitIsNull: TimerFailure()
}