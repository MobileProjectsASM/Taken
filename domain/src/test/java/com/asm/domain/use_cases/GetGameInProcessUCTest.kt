package com.asm.domain.use_cases

import com.asm.domain.repositories.GameRepository
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest

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

    /*@Test
    fun `test process when getGameInProcess return a Failure`() = runTest {
        //Arrange
        coEvery { gameRepository.getGameInProcess(ofType(String::class)) } returns Error.NetworkConnection.toLeft()

        //Act
        val result = getGameInProcessUC.execute("")

        //Asserts
        coVerify(exactly = 1) { gameRepository.getGameInProcess(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is com.asm.domain.errors.Failure.Error.NetworkConnection)
    }

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
        assert(failure is com.asm.domain.errors.Failure.Error.UnknownError)
    }

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
        assert(failure is GameError.ThereIsNotGameInProcess)
    }

    @Test
    fun `test process when all is right`() = runTest {
        //Arranges
        val expectedValue = Game(
            LevelInfo(
                1,
                "",
                ""
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
    }*/
}