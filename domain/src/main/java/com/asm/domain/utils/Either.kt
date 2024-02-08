package com.asm.domain.utils

sealed class Either<out L, out R> {
    data class Left<out L>(val l: L) : Either<L, Nothing>()
    data class Right<out R>(val r: R) : Either<Nothing, R>()

    companion object {
        suspend fun <Right> catch(
            operation: suspend () -> Right
        ): Either<Exception, Right> = try {
            operation().toRight()
        } catch (e: Exception) {
            e.toLeft()
        }
    }

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <T> fold(fnL: (L) -> T, fnR: (R) -> T): T = when (this) {
        is Left -> fnL(l)
        is Right -> fnR(r)
    }

    suspend fun <T> coFold(fnL: suspend (L) -> T, fnR: suspend (R) -> T): T = when (this) {
        is Left -> fnL(l)
        is Right -> fnR(r)
    }
}

fun <T> T.toRight(): Either.Right<T> = Either.Right(this)

fun <T> T.toLeft(): Either.Left<T> = Either.Left(this)