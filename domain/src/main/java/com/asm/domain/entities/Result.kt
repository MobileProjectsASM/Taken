package com.asm.domain.entities

sealed class Result<out Data, out Error> {
    data class Successful<out Data>(val data: Data): Result<Data, Nothing>()
    data class Unsuccessful<out Error>(val failure: Error): Result<Nothing, Error>()
}

fun <Data, Error> Result<Data, Error>.asSuccessful() = this as Result.Successful