package com.asm.domain.entities

import com.asm.domain.errors.TimerError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.math.round
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
        assert(result.isFailure)
        val failure = result.asFailure().failure
        assert(failure is TimerError.TimeInitIsNull)
    }

    @Test
    fun `test when time is right`() = runBlocking {
        //Arrange
        val seconds = 60L
        timer.setTime(seconds * 1_000)
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
        assert(result.isSuccessful)
    }

    @Test
    fun `test when pause time before timeout`() = runBlocking {
        //Arrange
        val seconds = 60L
        val simulatedProcessTime = 38L
        val expectedLeftSeconds = seconds - simulatedProcessTime
        timer.setTime(seconds * 1_000)

        //Act
        launch {
            val result = timer.start(initTimer = {}, inProcess = {}, timeOut = {})
            assert(result.isSuccessful)
        }
        //Wait 10 seconds and pause timer
        delay(simulatedProcessTime * 1_000)
        val leftTime = timer.pause()


        //Asserts
        assertNotNull(leftTime)
        val leftSeconds = round(leftTime / 1000.0).toLong()
        assertEquals(expectedLeftSeconds, leftSeconds)
    }

    @Test
    fun `test when pause timer and already it timeout`() = runBlocking {
        //Arrange
        val seconds = 20L
        val simulatedProcessTime = 38L
        timer.setTime(seconds * 1_000)

        //Act
        launch {
            val result = timer.start(initTimer = {}, inProcess = {}, timeOut = {})
            assert(result.isSuccessful)
        }
        //Wait 10 seconds and pause timer
        delay(simulatedProcessTime * 1_000)
        val leftTime = timer.pause()


        //Asserts
        assertNull(leftTime)
    }
}