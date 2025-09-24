package com.asm.domain.errors

import com.asm.domain.entities.Result

sealed class GeneralError {
    data object NetworkError: GeneralError()
    data object Unknown: GeneralError()
    data class ServerError(val message: String): GeneralError()
}

sealed class GamerError {
    data object GamerNotExists: GamerError()
    data class General(val generalError: GeneralError): GamerError()
}

fun GeneralError.toGamerError() = GamerError.General(this)

fun GamerError.toUnsuccessful() = Result.Unsuccessful(this)


sealed class TimerFailure {
    data object TimeInitIsNull: TimerFailure()
    data class General(val generalFailure: GeneralError): TimerFailure()
}