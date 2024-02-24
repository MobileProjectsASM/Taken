package com.asm.domain.entities

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TableManagementTest {

    private lateinit var tableManagement: TableManagement

    @BeforeTest
    fun onBefore() {
        tableManagement = TableManagement()
    }

    @Test
    fun `test the process that initializes the board`() {
        //Arrange
        val level = Level(
            "Level 1",
            1,
            "",
            Difficulty.EASY,
            TimeMetrics(0, 0, 0, 0, 0),
            MovementsMetrics(0, 0, 0, 0,0),
            arrayOf(
                arrayOf(Box(1, 255), Box(2, 4294967295), Box(3, 255), Box(4, 4294967295)),
                arrayOf(Box(5, 4294967295), Box(6, 255), Box(7, 4294967295), Box(8, 255)),
                arrayOf(Box(9, 255), Box(10, 4294967295), Box(11, 255), Box(12, 4294967295)),
                arrayOf(Box(13, 4294967295), Box(14, 255), Box(15, 4294967295), Box(0, 255))
            )
        )
        val initOrder = arrayOf(
            arrayOf(15, 11, 14, 12),
            arrayOf(6, 8, 13, 9),
            arrayOf(1, 10, 2, 4),
            arrayOf(0, 5, 3, 7)
        )
        val expectedTable = arrayOf(
            arrayOf(Box(15, 4294967295), Box(11, 255), Box(14, 255), Box(12, 4294967295)),
            arrayOf(Box(6, 255), Box(8, 255), Box(13, 4294967295), Box(9, 255)),
            arrayOf(Box(1, 255), Box(10, 4294967295), Box(2, 4294967295), Box(4, 4294967295)),
            arrayOf(Box(0, 255), Box(5, 4294967295), Box(3, 255), Box(7, 4294967295))
        )

        //Act
        val initTable = tableManagement.getInitTable(level, initOrder)

        //Asserts
        assertNotNull(initTable)
        assertEquals(expectedTable.size, initTable.size)
        for ((i, _) in expectedTable.withIndex()) {
            assertEquals(expectedTable[i].size, initTable[i].size)
            for ((j, _) in expectedTable[i].withIndex()) {
                assertEquals(expectedTable[i][j], initTable[i][j])
            }
        }
    }
}