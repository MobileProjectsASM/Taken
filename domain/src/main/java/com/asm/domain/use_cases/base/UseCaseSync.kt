package com.asm.domain.use_cases.base

import com.asm.domain.entities.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCaseSync<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Result<Type>

    suspend fun execute(params: Params): Result<Type> = withContext(Dispatchers.IO) {
        run(params)
    }

}