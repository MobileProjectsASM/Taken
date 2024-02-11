package com.asm.domain.use_cases

import com.asm.domain.entities.Difficulty
import com.asm.domain.entities.Level
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLevelUCTest {
    private lateinit var getLevelUC: GetLevelUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var levelRepository: LevelRepository

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, true)
        getLevelUC = GetLevelUC(logger, levelRepository)
    }

    @Test
    fun `test process when process throws an exception`() = runTest {
        //Arrange
        coEvery { levelRepository.getLevelByOrder(ofType(Int::class)) } throws Exception("Another exception")
        coEvery { logger.logE(any()) } just runs

        //Act
        val result = getLevelUC.execute(0)

        //Asserts
        coVerify(exactly = 1) { levelRepository.getLevelByOrder(ofType(Int::class)) }
        coVerify(exactly = 1) { logger.logE(any()) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.UnknownError)
    }

    @Test
    fun `test process when getLevelByOrder return a failure`() = runTest {
        //Arrange
        coEvery { levelRepository.getLevelByOrder(ofType(Int::class)) } returns Failure.NetworkConnection.toLeft()

        //Act
        val result = getLevelUC.execute(1)

        //Arrange
        coVerify(exactly = 1) { levelRepository.getLevelByOrder(ofType(Int::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.NetworkConnection)
    }

    @Test
    fun `test process when all is right`() = runTest {
        //Arrange
        val expectedLevel = Level(
            "",
            1,
            "",
            Difficulty.EASY,
            120,
            40,
            arrayOf()
        )
        coEvery { levelRepository.getLevelByOrder(ofType(Int::class)) } returns expectedLevel.toRight()

        //Act
        val result = getLevelUC.execute(1)

        //Asserts
        coVerify(exactly = 1) { levelRepository.getLevelByOrder(ofType(Int::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedLevel, value)
    }
}