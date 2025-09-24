package com.asm.domain.errors

import com.asm.domain.entities.Result

sealed class GeneralError {
    data object NetworkError: GeneralError()
    data object Unknown: GeneralError()
    data class ServerError(val message: String): GeneralError()
    data class ClientError(val code: String): GeneralError()
}

sealed class GamerError {
    data object GamerNotExists: GamerError()
    data class General(val generalError: GeneralError): GamerError()
}

sealed class SessionError {
    data object SessionNotExists: SessionError()
    data class General(val generalError: GeneralError): SessionError()
}

enum class GamerError2 {

}

fun GeneralError.toGamerError() = GamerError.General(this)
fun GeneralError.toSessionError() = SessionError.General(this)
fun GeneralError.toUnsuccessful() = Result.Unsuccessful(this)

fun GamerError.toUnsuccessful() = Result.Unsuccessful(this)
fun SessionError.toUnsuccessful() = Result.Unsuccessful(this)


sealed class TimerFailure {
    data object TimeInitIsNull: TimerFailure()
    data class General(val generalFailure: GeneralError): TimerFailure()
}