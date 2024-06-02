package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Error
import com.asm.domain.errors.RegisterError
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
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
    private lateinit var logger: Logger

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        signInUC = CreateGamerUC(
            gamerRepository,
            levelRepository,
            gameRepository,
            logger,
        )
    }

    @Test
    fun `test the registration process when there is no internet connection`() = runTest {
        //Arrange
        val param = Gamer(
            gamerId = "Abc",
            gamerNickName = "Arturo",
            gamerAge = 26,
            gamerCountry = "MX",
            gamerImage = ""
        )
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns Either.Left(
            Error.NetworkConnection
        )

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is com.asm.domain.errors.Failure.Error.NetworkConnection)
    }

    @Test
    fun `test the registration process when a user has already been registered`() = runTest {
        //Arrange
        val param = Gamer(
            gamerId = "Abc",
            gamerNickName = "Arturo",
            gamerAge = 26,
            gamerCountry = "MX",
            gamerImage = ""
        )
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns Either.Right(
            true
        )

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is RegisterError.GamerExists)
    }

    @Test
    fun `test the registration process when there is no internet connection 2`() = runTest {
        //Arrange
        val param = Gamer(
            gamerId = "Abc",
            gamerNickName = "Arturo",
            gamerAge = 26,
            gamerCountry = "MX",
            gamerImage = ""
        )
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns Either.Right(
            false
        )
        coEvery { gamerRepository.registerGamer(param) } returns Either.Left(Error.NetworkConnection)

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is com.asm.domain.errors.Failure.Error.NetworkConnection)
    }

    @Test
    fun `test the registration process when all is right`() = runTest {
        //Arrange
        val param = Gamer(
            gamerId = "Abc",
            gamerNickName = "Arturo",
            gamerAge = 26,
            gamerCountry = "MX",
            gamerImage = ""
        )
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns Either.Right(
            false
        )
        coEvery { gamerRepository.registerGamer(param) } returns Either.Right(Completed)

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        assert(result.isRight)
    }

    @Test
    fun `test the registration process when occur other exception`() = runTest {
        //Arrange
        val otherException = Exception("Other exception")
        val param = Gamer(
            gamerId = "Abc",
            gamerNickName = "Arturo",
            gamerAge = 26,
            gamerCountry = "MX",
            gamerImage = ""
        )
        coEvery { gamerRepository.checkIfGamerExists(ofType(String::class)) } returns Either.Right(
            false
        )
        coEvery { gamerRepository.registerGamer(param) } throws otherException
        coEvery { logger.logE(any()) } just runs


        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        coVerify(exactly = 1) { logger.logE(any()) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is com.asm.domain.errors.Failure.Error.UnknownError)
    }
}