package com.asm.domain.errors

class AuthProcessException(message: String, cause: Throwable? = null): Exception(message, cause)