package com.asm.domain.entities

import com.asm.domain.errors.TimerFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

class Timer {
    private var time: Long? = null
    private val timeSource: TimeSource.Monotonic = TimeSource.Monotonic
    private var initMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var job: Job? = null

    fun setTime(time: Long) {
        this.time = time
    }

    suspend fun start(
        initTimer: (Long) -> Unit,
        inProcess: (Long) -> Unit,
        timeOut: () -> Unit
    ): Result<Unit, TimerFailure> = coroutineScope {
        if (time == null) return@coroutineScope Result.Unsuccessful(TimerFailure.TimeInitIsNull)
        job = launch {
            initMark = timeSource.markNow()
            initTimer.invoke(time!!)
            do {
                delay(1_000)
                val periodMark = timeSource.markNow()
                val elapsedTime = periodMark.minus(initMark!!).inWholeMilliseconds
                inProcess.invoke(time!! - elapsedTime)
            } while (elapsedTime < time!!)
            timeOut()
        }
        return@coroutineScope Result.Successful(Unit)
    }

    fun pause(): Long? {
        if (job == null) return null
        if (job!!.isCompleted) return null
        val pauseMark = timeSource.markNow()
        job!!.cancel()
        job = null
        val elapsedTime = pauseMark.minus(initMark!!).inWholeMilliseconds
        return time!! - elapsedTime
    }
}