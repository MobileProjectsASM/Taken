package com.asm.domain.use_cases

import com.asm.domain.errors.GamerFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Logger
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetGamerUCTest {
    /*private lateinit var getGamerUC: GetGamerUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var gamerRepository: GamerRepository

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, true)
        getGamerUC = GetGamerUC(logger, gamerRepository)
    }

    @Test
    fun `test the process when getGamerById return a Failure`() = runTest {
        //Arrange
        coEvery { gamerRepository.getGamerById(ofType(String::class)) } returns GamerFailure.LoginNotExists.toUnsuccessful()

        //Act
        val result = getGamerUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamerRepository.getGamerById(ofType(String::class)) }
        assert(result.isUnsuccessful)
    }

    @Test
    fun `test the process when process throws error`() = runTest {
        //Arrange
        coEvery { gamerRepository.getGamerById(ofType(String::class)) } throws Exception("Another error")
        coEvery { logger.logE(ofType(String::class), ofType(Exception::class)) } just runs

        //Act
        val result = getGamerUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamerRepository.getGamerById(ofType(String::class)) }
        coVerify(exactly = 1) { logger.logE(ofType(String::class), ofType(Exception::class)) }
        assert(result.isUnsuccessful)
    }*/
}