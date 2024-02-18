package com.asm.domain.entities

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SolutionValidatorTest {

    private lateinit var solutionValidator: SolutionValidator

    @BeforeTest
    fun onBefore() {
        solutionValidator = SolutionValidator()
    }

    @Test
    fun `test validateSolution when the solution is incorrect`() {
        //Arrange
        val level = Level(
            "Level 1",
            1,
            "",
            Difficulty.EASY,
            500,
            50,
            arrayOf(
                arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
                arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
                arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 4294967295)),
                arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 255))
            )
        )
        val solution = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(0, 4294967295)),
            arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(12, 255))
        )

        //Act
        val isValid = solutionValidator.validateSolution(level, solution)

        //Asserts
        assertFalse(isValid)
    }

    @Test
    fun `test validateSolution when the solution is right`() {
        //Arrange
        val level = Level(
            "Level 1",
            1,
            "",
            Difficulty.EASY,
            500,
            50,
            arrayOf(
                arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
                arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
                arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 4294967295)),
                arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 255))
            )
        )
        val solution = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 255)),
            arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 4294967295))
        )

        //Act
        val isValid = solutionValidator.validateSolution(level, solution)

        //Asserts
        assertTrue(isValid)
    }
}