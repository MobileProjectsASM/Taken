package com.asm.domain.use_cases

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
import kotlinx.coroutines.test.runTest
import org.junit.BeforeClass
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetMainGamesUCTest {
    private lateinit var getMainGamesUC: GetMainGamesUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var gamesRepository: GameRepository

    companion object {
        var fakeGames: FakeGames? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            fakeGames = FakeGames()
        }
    }

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, true)
        getMainGamesUC = GetMainGamesUC(logger, gamesRepository)
    }

    @Test
    fun `test process when getGamesByGamerId return Failure`() = runTest {
        //Arrange
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns Failure.NetworkConnection.toLeft()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.NetworkConnection)
    }

    @Test
    fun `test process when throws an exception`() = runTest {
        //Arrange
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } throws Exception("Another error")
        coEvery { logger.logE(any()) } just runs

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        coVerify(exactly = 1) { logger.logE(any()) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.UnknownError)
    }


    @Test
    fun `test process when there is a game with GameStatus_Process`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.moreThanOneGameInProcess
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is GameFailure.ThereIsGameInProcess)
    }

    @Test
    fun `test process when there are more than one games with GameStatus_New`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.moreThanOneNewGame
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is GameFailure.MoreThanOneNewGame)
    }

    @Test
    fun `test process when there are more than one game with GameStatus_Lock`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.moreThanOneLockGame
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is GameFailure.MoreThanOneLockGame)
    }
}