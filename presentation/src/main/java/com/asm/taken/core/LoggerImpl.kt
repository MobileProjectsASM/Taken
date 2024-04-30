package com.asm.taken.core

import android.util.Log
import com.asm.domain.utils.Logger
import javax.inject.Inject

class LoggerImpl @Inject constructor(): Logger {
    override fun logI(message: () -> String) {
        Log.i("DOMAIN", message())
    }

    override fun logE(throwable: () -> Throwable) {
        Log.e("DOMAIN", throwable().stackTraceToString())
    }
}