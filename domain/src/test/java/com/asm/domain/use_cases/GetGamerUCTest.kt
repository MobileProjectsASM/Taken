package com.asm.domain.use_cases

import com.asm.domain.errors.GamerFailure
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
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

class GetGamerUCTest {
    private lateinit var getGamerUC: GetGamerUC

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var gamerRepository: GamerRepository

    @BeforeTest
    fun onBefore() {
        MockKAnnotations.init(this, true)
        getGamerUC = GetGamerUC(logger, gamerRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test the process when getGamerById return a Failure`() = runTest {
        //Arrange
        coEvery { gamerRepository.getGamerById(ofType(String::class)) } returns GamerFailure.GamerNotExists.toLeft()

        //Act
        val result = getGamerUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamerRepository.getGamerById(ofType(String::class)) }
        assert(result.isLeft)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test the process when process throws error`() = runTest {
        //Arrange
        coEvery { gamerRepository.getGamerById(ofType(String::class)) } throws Exception("Another error")
        coEvery { logger.logE(any()) } just runs

        //Act
        val result = getGamerUC.execute("ABCDE")

        //Asserts
        coVerify(exactly = 1) { gamerRepository.getGamerById(ofType(String::class)) }
        coVerify(exactly = 1) { logger.logE(any()) }
        assert(result.isLeft)
    }
}