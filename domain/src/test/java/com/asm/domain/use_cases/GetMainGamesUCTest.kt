package com.asm.domain.use_cases

import com.asm.domain.repositories.GameRepository
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.BeforeClass
import kotlin.test.BeforeTest

class GetMainGamesUCTest {
    private lateinit var getMainGamesUC: GetMainGamesUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var gamesRepository: GameRepository

    /*companion object {
        var fakeGames: FakeGames? = null
        var gamesExpected: GamesExpected? = null

        @BeforeClass
        @JvmStatic
        fun setup() {
            fakeGames = FakeGames()
            gamesExpected = GamesExpected()
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
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns Error.NetworkConnection.toLeft()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is com.asm.domain.errors.Failure.Error.NetworkConnection)
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
        assert(failure is com.asm.domain.errors.Failure.Error.UnknownError)
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
        assert(failure is GameError.ThereIsGameInProcess)
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
        assert(failure is GameError.MoreThanOneNewGame)
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
        assert(failure is GameError.MoreThanOneLockGame)
    }

    @Test
    fun `test process when is new Gamer`() = runTest {
        //Arrange
        val initialGames = fakeGames!!.initialGames
        val expectedValue = gamesExpected!!.expectedInitialGames
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns initialGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABDCE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedValue, value)
    }

    @Test
    fun `test when the there is games won`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.firstLevelWin
        val expectedValue = gamesExpected!!.firstLevelWinExpected
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedValue, value)
    }

    @Test
    fun `test when the there are multiple levels won`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.multipleLevelsWin
        val expectedValue = gamesExpected!!.multipleLevelsWinExpected
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedValue, value)
    }

    @Test
    fun `test when the there isn't levels lock`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.thereIsNotLevelLock
        val expectedValue = gamesExpected!!.thereIsNotLevelLockExpected
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedValue, value)
    }

    @Test
    fun `test when the all levels are won`() = runTest {
        //Arrange
        val fakeGames = fakeGames!!.allLevelsWon
        val expectedValue = gamesExpected!!.allLevelsWonExpected
        coEvery { gamesRepository.getGamesByGamerId(ofType(String::class)) } returns fakeGames.toRight()

        //Act
        val result = getMainGamesUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamesRepository.getGamesByGamerId(ofType(String::class)) }
        assert(result.isRight)
        val value = (result as Either.Right).r
        assertEquals(expectedValue, value)
    }*/
}