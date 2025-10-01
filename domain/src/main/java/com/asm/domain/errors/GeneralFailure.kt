package com.asm.domain.errors

import com.asm.domain.entities.Result

sealed class GeneralError {
    data object NetworkError: GeneralError()
    data object Unknown: GeneralError()
    data class ServerError(val message: String): GeneralError()
    data class ClientError(val message: String): GeneralError()
}

fun GeneralError.toUnsuccessful() = Result.Unsuccessful(this)


sealed class TimerFailure {
    data object TimeInitIsNull: TimerFailure()
    data class General(val generalFailure: GeneralError): TimerFailure()
}