package com.asm.data.sources.hardware

interface Connection {
    fun thereIsInternetConnection(): Boolean
}