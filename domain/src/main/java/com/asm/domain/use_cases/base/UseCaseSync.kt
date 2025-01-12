package com.asm.domain.use_cases.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCaseSync<out Result, in Params> {
    abstract suspend fun run(params: Params): Result

    suspend fun execute(params: Params): Result = withContext(Dispatchers.IO) {
        run(params)
    }

}