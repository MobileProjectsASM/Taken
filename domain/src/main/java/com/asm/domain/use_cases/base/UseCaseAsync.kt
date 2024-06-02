package com.asm.domain.use_cases.base

import com.asm.domain.entities.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class UseCaseAsync<out Type, in Params> where Type : Any {
    abstract suspend fun run(params: Params): Result<Type>

    operator fun invoke(
        params: Params,
        scope: CoroutineScope = MainScope(),
        onResult: (Result<Type>) -> Unit = {}
    ) {
        scope.launch {
            val deferredJob = async(Dispatchers.IO) {
                run(params)
            }
            onResult(deferredJob.await())
        }
    }

}