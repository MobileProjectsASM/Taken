package com.asm.domain.use_cases

import com.asm.domain.entities.Difficulty
import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Level
import com.asm.domain.entities.LevelInfo
import com.asm.domain.entities.MovementsMetrics
import com.asm.domain.entities.TimeMetrics
import com.asm.domain.entities.asUnsuccessful
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.errors.RegisterGeneralFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class CreateGamerUCTest {

    private lateinit var signInUC: CreateGamerUC

    @MockK
    private lateinit var gamerRepository: GamerRepository

    @MockK
    private lateinit var levelRepository: LevelRepository

    @MockK
    private lateinit var gameRepository: GameRepository

    @MockK
    private lateinit var multimediaRepository: MultimediaRepository

    @MockK
    private lateinit var connectionRepository: ConnectionRepository

    @MockK
    private lateinit var logger: Logger

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        signInUC = CreateGamerUC(
            gamerRepository,
            levelRepository,
            gameRepository,
            multimediaRepository,
            connectionRepository,
            logger,
        )
    }


    @Test
    fun `test the registration process when process internet connection fail`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when there is no internet connection`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns false.toSuccessful()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        assert(result.isUnsuccessful)
        val failure = result.asUnsuccessful().generalFailure
        assert(failure is GeneralFailure.NetworkConnection)
    }

    @Test
    fun `test the registration process when process verify gamer exists fail`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when gamer already exists`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns true.toSuccessful()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        assert(result.isUnsuccessful)
        val failure = result.asUnsuccessful().generalFailure
        assert(failure is RegisterGeneralFailure.GamerExists)
    }

    @Test
    fun `test the registration process when user set own image`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = CreateGamerUC.InfoImage(
                "",
                ""
            )
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.getDefaultUserImage() }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when user not choose image`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.getDefaultUserImage() } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.getDefaultUserImage() }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when register gamer fail`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.getDefaultUserImage() } returns "/example/path".toSuccessful()
        coEvery { gamerRepository.registerGamer(ofType(Gamer::class)) } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.getDefaultUserImage() }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when download init levels fail`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.getDefaultUserImage() } returns "/example/path".toSuccessful()
        coEvery { gamerRepository.registerGamer(ofType(Gamer::class)) } returns Completed.toSuccessful()
        coEvery { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.getDefaultUserImage() }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        coVerify(exactly = 1) { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when save games fail`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        val initLevels = listOf(
            Level(
                "level_1",
                "Level 1",
                1,
                "",
                Difficulty.EASY,
                TimeMetrics(0,0,0,0,0),
                MovementsMetrics(0,0,0,0,0),
                arrayOf()
            ),
            Level(
                "level_2",
                "Level 2",
                2,
                "",
                Difficulty.EASY,
                TimeMetrics(0,0,0,0,0),
                MovementsMetrics(0,0,0,0,0),
                arrayOf()
            )
        )
        val initGames = listOf(
            Game(
                "",
                LevelInfo("level_1", "Level 1",""),
                GameStatus.Win(30, 20, 23.0),
            ),
            Game(
                "",
                LevelInfo("level_2", "Level 2",""),
                GameStatus.Win(35, 15, 25.0),
            ),
        )

        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.getDefaultUserImage() } returns "/example/path".toSuccessful()
        coEvery { gamerRepository.registerGamer(ofType(Gamer::class)) } returns Completed.toSuccessful()
        coEvery { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) } returns initLevels.toSuccessful()
        coEvery { gameRepository.saveGamerGames(any(), ofType(String::class)) } returns GeneralFailure.UnknownGeneralFailure.toFailure()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.getDefaultUserImage() }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        coVerify(exactly = 1) { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) }
        coVerify(exactly = 1) { gameRepository.saveGamerGames(any(), ofType(String::class)) }

        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the registration process when save games successful`() = runTest {
        //Arrange
        val param = CreateGamerUC.GamerParams(
            gamerId = "Abc",
            nickName = "Arturo",
            age = 26,
            country = "MX",
            image  = null
        )
        val initLevels = listOf(
            Level(
                "level_1",
                "Level 1",
                1,
                "",
                Difficulty.EASY,
                TimeMetrics(0,0,0,0,0),
                MovementsMetrics(0,0,0,0,0),
                arrayOf()
            ),
            Level(
                "level_2",
                "Level 2",
                2,
                "",
                Difficulty.EASY,
                TimeMetrics(0,0,0,0,0),
                MovementsMetrics(0,0,0,0,0),
                arrayOf()
            )
        )
        val initGames = listOf(
            Game(
                "",
                LevelInfo("level_1", "Level 1",""),
                GameStatus.Win(30, 20, 23.0),
            ),
            Game(
                "",
                LevelInfo("level_2", "Level 2",""),
                GameStatus.Win(35, 15, 25.0),
            ),
        )

        coEvery { connectionRepository.isNetworkAvailable() } returns true.toSuccessful()
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns false.toSuccessful()
        coEvery { multimediaRepository.getDefaultUserImage() } returns "/example/path".toSuccessful()
        coEvery { gamerRepository.registerGamer(ofType(Gamer::class)) } returns Completed.toSuccessful()
        coEvery { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) } returns initLevels.toSuccessful()
        coEvery { gameRepository.saveGamerGames(any(), ofType(String::class)) } returns Completed.toSuccessful()

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { connectionRepository.isNetworkAvailable() }
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 0) { multimediaRepository.uploadUserImage(ofType(String::class), ofType(String::class), ofType(String::class)) }
        coVerify(exactly = 1) { multimediaRepository.getDefaultUserImage() }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        coVerify(exactly = 1) { levelRepository.downloadLevelsByOrderCriteria(listOf(1, 2)) }
        coVerify(exactly = 1) { gameRepository.saveGamerGames(any(), ofType(String::class)) }

        assert(result.isSuccessful)
        val data = result.asSuccessful().data
        assert(data == Completed)
    }
}