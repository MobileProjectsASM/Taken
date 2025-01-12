package com.asm.domain.entities

sealed class Result<out Data, out Failure> {
    data class Successful<out Data>(val data: Data): Result<Data, Nothing>()
    data class Unsuccessful<out Failure>(val failure: Failure): Result<Nothing, Failure>()
}

fun <Data, Failure> Result<Data, Failure>.asSuccessful() = this as Result.Successful