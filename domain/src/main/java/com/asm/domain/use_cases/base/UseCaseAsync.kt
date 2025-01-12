package com.asm.domain.use_cases.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class UseCaseAsync<out Result, in Params> {
    abstract suspend fun run(params: Params): Result

    operator fun invoke(
        params: Params,
        scope: CoroutineScope = MainScope(),
        onResult: (Result) -> Unit = {}
    ) {
        scope.launch {
            val deferredJob = async(Dispatchers.IO) {
                run(params)
            }
            onResult(deferredJob.await())
        }
    }

}