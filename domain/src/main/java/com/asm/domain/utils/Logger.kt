package com.asm.domain.utils

interface Logger {
    fun logI(message: () -> String)
    fun logE(throwable: () -> Throwable)
}