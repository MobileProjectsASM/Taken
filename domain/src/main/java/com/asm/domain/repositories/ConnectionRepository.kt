package com.asm.domain.repositories

import com.asm.domain.entities.Result

interface ConnectionRepository {
    suspend fun thereIsInternetConnection(): Result<Boolean>
}