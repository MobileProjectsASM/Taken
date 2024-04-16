package com.asm.domain.use_cases.base

import com.asm.domain.errors.Failure
import com.asm.domain.utils.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCaseSync<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Either<Failure, Type>

    suspend fun execute(params: Params): Either<Failure, Type> = withContext(Dispatchers.IO) {
        run(params)
    }

}