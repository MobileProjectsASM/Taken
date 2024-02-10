package com.asm.domain.use_cases

import com.asm.domain.entities.Difficulty
import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Level
import com.asm.domain.errors.Failure
import com.asm.domain.errors.GameFailure
import com.asm.domain.repositories.GameRepository
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetGameInProcessUCTest {

    private lateinit var getGameInProcessUC: GetGameInProcessUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var gameRepository: GameRepository

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, true)
        getGameInProcessUC = GetGameInProcessUC(logger, gameRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test process when getGameInProcess return a Failure`() = runTest {
        //Arrange
        coEvery { gameRepository.getGameInProcess(ofType(String::class)) } returns Failure.NetworkConnection.toLeft()

        //Act
        val result = getGameInProcessUC.execute("")

        //Asserts
        coVerify(exactly = 1) { gameRepository.getGameInProcess(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.NetworkConnection)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test process when an exception occurs in the process`() = runTest {
        //Arrange
        coEvery { gameRepository.getGameInProcess(ofType(String::class)) } throws Exception("Another exception")
        coEvery { logger.logE(any()) } just runs

        //Act
        val result = getGameInProcessUC.execute("")

        //Asserts
        coVerify(exactly = 1) { gameRepository.getGameInProcess(ofType(String::class)) }
        coVerify(exactly = 1) { logger.logE(any()) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.UnknownError)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test process when getGameInProcess return null value`() = runTest {
        //Arrange
        coEvery { gameRepository.getGameInProcess(ofType(String::class)) } returns Either.Right(null)

        //Act
        val result = getGameInProcessUC.execute("")

        //Asserts
        coVerify(exactly = 1) { gameRepository.getGameInProcess(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is GameFailure.ThereIsNotGameInProcess)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test process when all is right`() = runTest {
        //Arranges
        val expectedValue = Game(
            Level(
                "",
                1,
                "",
                Difficulty.EASY,
                100,
                1000,
                arrayOf()
            ),
            GameStatus.New,
            arrayOf()
        )
        coEvery { gameRepository.getGameInProcess(ofType(String::class)) } returns expectedValue.toRight()

        //Act
        val result = gameRepository.getGameInProcess("")

        //Asserts
        coVerify { gameRepository.getGameInProcess(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertNotNull(value)
        assertEquals(expectedValue, value)
    }
}