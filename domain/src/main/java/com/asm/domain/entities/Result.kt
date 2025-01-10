package com.asm.domain.entities

import com.asm.domain.errors.GeneralFailure

sealed class Result<out T> {
    data class Successful<out T>(val data: T): Result<T>()
    data class Unsuccessful<out T>(val generalFailure: GeneralFailure): Result<T>()

    val isSuccessful get() = this is Successful<T>
    val isUnsuccessful get() = this is Unsuccessful<T>

}

fun <T, S> Result.Unsuccessful<T>.toUnsuccessful(): Result.Unsuccessful<S> = Result.Unsuccessful(this.generalFailure)
fun <T> Result<T>.asSuccessful(): Result.Successful<T> = this as Result.Successful<T>
fun <T> Result<T>.asUnsuccessful(): Result.Unsuccessful<T> = this as Result.Unsuccessful<T>
fun <T> GeneralFailure.toUnsuccessful(): Result.Unsuccessful<T> = Result.Unsuccessful(this)
fun <T> T.toSuccessful(): Result.Successful<T> = Result.Successful(this)