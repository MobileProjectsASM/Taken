package com.asm.data.sources.hardware

interface ConnectionSource {
    suspend fun isNetworkAvailable(): Boolean
}