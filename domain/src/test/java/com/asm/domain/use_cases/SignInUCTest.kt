package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.errors.RegisterFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.lang.Exception
import kotlin.test.BeforeTest
import kotlin.test.Test

class SignInUCTest {

    private lateinit var signInUC: SignInUC

    @MockK
    private lateinit var gamerRepository: GamerRepository

    @MockK
    private lateinit var logger: Logger

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        signInUC = SignInUC(gamerRepository, logger)
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
            Failure.NetworkConnection
        )

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.NetworkConnection)
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
        assert(failure is RegisterFailure.GamerExists)
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
        coEvery { gamerRepository.registerGamer(param) } returns Either.Left(Failure.NetworkConnection)

        //Act
        val result = signInUC.execute(param)

        //Asserts
        coVerify(exactly = 1) { gamerRepository.checkIfGamerExists(ofType(String::class)) }
        coVerify(exactly = 1) { gamerRepository.registerGamer(ofType(Gamer::class)) }
        assert(result.isLeft)
        val failure = (result as Either.Left).l
        assert(failure is Failure.NetworkConnection)
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
        assert(failure is Failure.UnknownError)
    }
}