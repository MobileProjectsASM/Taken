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

sealed class RegisterFailure {
    data object GamerExists: RegisterFailure()
    data class General(val generalFailure: GeneralFailure): RegisterFailure()
}

sealed class GamerFailure {
    data object GamerNotExists: GamerFailure()
    data class General(val generalFailure: GeneralFailure): GamerFailure()
}

sealed class GameFailure {
    data object ThereIsNotGameInProcess: GameFailure()
    data object ThereIsGameInProcess: GameFailure()
    data object MoreThanOneNewGame: GameFailure()
    data object MoreThanOneLockGame: GameFailure()
    data class General(val generalFailure: GeneralFailure): GameFailure()
}

sealed class TimerFailure {
    data object TimeInitIsNull: TimerFailure()
    data class General(val generalFailure: GeneralFailure): TimerFailure()
}

fun GeneralFailure.toGamerFailure() = GamerFailure.General(this)
fun GeneralFailure.toRegisterFailure() = RegisterFailure.General(this)