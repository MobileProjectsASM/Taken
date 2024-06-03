package com.asm.data.sources.hardware.impl

import android.util.Log
import com.asm.domain.utils.Logger
import javax.inject.Inject

class LoggerSourceMobile @Inject constructor(): Logger {
    override fun logI(location: String, message: String) {
        Log.i(location, message)
    }

    override fun logE(location: String, throwable: Throwable) {
        Log.e(location, throwable.stackTraceToString())
    }
}