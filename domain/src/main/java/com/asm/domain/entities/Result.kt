package com.asm.domain.entities

import com.asm.domain.errors.Failure

sealed class Result<out T> {
    data class Successful<out T>(val data: T): Result<T>()
    data class Failure<out T>(val failure: com.asm.domain.errors.Failure): Result<T>()

    val isSuccessful get() = this is Successful<T>
    val isFailure get() = this is Failure<T>

}

fun <T, S> Result.Failure<T>.toFailure(): Result.Failure<S> = Result.Failure(this.failure)
fun <T> Result<T>.asSuccessful(): Result.Successful<T> = this as Result.Successful<T>
fun <T> Result<T>.asFailure(): Result.Failure<T> = this as Result.Failure<T>
fun <T> Failure.toFailure(): Result.Failure<T> = Result.Failure(this)
fun <T> T.toSuccessful(): Result.Successful<T> = Result.Successful(this)