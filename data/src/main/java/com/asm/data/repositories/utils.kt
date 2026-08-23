package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.Failure

suspend fun <T> ConnectionSource.ifConnectionIsAvailableRun(execute: suspend () -> Result<T, Failure>): Result<T, Failure> {
    return if (isNetworkAvailable()) execute()
    else Result.Unsuccessful(Failure.SystemFailure.NETWORK_CONNECTION)
}