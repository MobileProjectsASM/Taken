package com.asm.domain.use_cases.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCaseSync<out Result, in Params> {
    protected abstract suspend fun run(params: Params): Result

    suspend fun execute(params: Params): Result = withContext(Dispatchers.IO) {
        run(params)
    }
}

abstract class UseCaseSyncWithoutParams<out Result> {
    protected abstract suspend fun run(): Result

    suspend fun execute(): Result = withContext(Dispatchers.IO) {
        run()
    }
}