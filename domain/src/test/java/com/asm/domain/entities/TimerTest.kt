package com.asm.domain.entities

import com.asm.domain.errors.TimerFailure
import com.asm.domain.utils.Either
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.math.round
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.TimeSource

class TimerTest {
    private lateinit var timer: Timer

    @BeforeTest
    fun onBefore() {
        timer = Timer()
    }

    @Test
    fun `test when time is null`(): Unit = runTest {
        //Act
        val result = timer.start({}, {}, {})

        //Asserts
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is TimerFailure.TimeInitIsNull)
    }

    @Test
    fun `test when time is right`() = runBlocking {
        //Arrange
        val seconds = 60L
        timer.setTime(seconds * 1000)
        val timeSource: TimeSource.Monotonic = TimeSource.Monotonic

        //Act
        var initMark: TimeSource.Monotonic.ValueTimeMark? = null
        var initPeriodMark: TimeSource.Monotonic.ValueTimeMark? = null
        val result = timer.start(initTimer = { initValue ->
            initMark = timeSource.markNow()
            val initSecond = round(initValue / 1000.0).toLong()
            assertEquals(seconds, initSecond)
            initPeriodMark = initMark
        }, inProcess = {
            val finishPeriodMark = timeSource.markNow()
            val elapsedTime = finishPeriodMark.minus(initPeriodMark!!).inWholeSeconds
            assertEquals(elapsedTime, 1)
            initPeriodMark = finishPeriodMark
        }, timeOut = {
            val finishMark = timeSource.markNow()
            val elapsedTime = finishMark.minus(initMark!!).inWholeSeconds
            assertEquals(elapsedTime, seconds)
        })

        //Asserts
        assert(result.isRight)
    }
}