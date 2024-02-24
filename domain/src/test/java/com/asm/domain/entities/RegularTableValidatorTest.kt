package com.asm.domain.entities

import junit.framework.TestCase.assertNull
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RegularTableValidatorTest {

    private lateinit var regularTableValidator: RegularTableValidator

    @BeforeTest
    fun onBefore() {
        regularTableValidator = RegularTableValidator()
    }

    @Test
    fun `test when position 0 is in corner an move position is right`() {
        //Arrange
        val table = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 4294967295)),
            arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 255))
        )
        val expectedValue = Pair(3, 3)

        //Act
        val result = regularTableValidator.findCellEmpty(table, 2, 3)

        //Asserts
        assertNotNull(result)
        assertEquals(expectedValue, result)
    }

    @Test
    fun `test when position 0 is in corner an move position is error`() {
        //Arrange
        val table = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 4294967295)),
            arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 255))
        )

        //Act
        val result = regularTableValidator.findCellEmpty(table, 1, 1)

        //Asserts
        assertNull(result)
    }

    @Test
    fun `test when position 0 is in center and position is right`() {
        //Arrange
        val table = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(0, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(6, 255), Box(11, 255), Box(12, 4294967295)),
            arrayOf(Box(13, 4294967295), Box(10, 4294967295), Box(14, 255), Box(15, 4294967295))
        )
        val expectedValue = Pair(1, 1)

        //Act
        val result = regularTableValidator.findCellEmpty(table, 0, 1)

        //Asserts
        assertNotNull(result)
        assertEquals(expectedValue, result)
    }

    @Test
    fun `test when position 0 is in center and position is error`() {
        //Arrange
        val table = arrayOf(
            arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
            arrayOf(Box(5, 4294967295), Box(0, 255), Box(7, 4294967295), Box(8, 255)),
            arrayOf(Box(9, 255), Box(6, 255), Box(11, 255), Box(12, 4294967295)),
            arrayOf(Box(13, 4294967295), Box(10, 4294967295), Box(14, 255), Box(15, 4294967295))
        )

        //Act
        val result = regularTableValidator.findCellEmpty(table, 0, 0)

        //Asserts
        assertNull(result)
    }
}