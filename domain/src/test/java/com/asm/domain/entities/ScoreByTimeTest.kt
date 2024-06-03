package com.asm.domain.entities

import com.asm.domain.entities.interfaces.LevelScore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreByTimeTest {

    private lateinit var levelScore: LevelScore
    private lateinit var level: Level

    @BeforeTest
    fun onBefore() {
        levelScore = ScoreByTime()
        level = Level(
            "level_1",
            "Level 1",
            1,
            "",
            Difficulty.EASY,
            TimeMetrics(300, 420, 540, 660, 780),
            MovementsMetrics(0, 0, 0, 0, 0),
            arrayOf()
        )
    }

    @Test
    fun `test when the movements exceed the maxMovements`() {
        //Arrange
        val gameStatus = GameStatus.Process(
            level.timeMetrics.percentage60 + 1,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Asserts
        assertEquals(0.0, result)
    }

    @Test
    fun `test when is in the range 100`() {
        //Arrange
        val time = (0..level.timeMetrics.percentage100).random()
        val gameStatus = GameStatus.Process(
            time,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Assert
        assertEquals(100.0, result)
    }

    @Test
    fun `test when is in the range 90`() {
        //Arrange
        val time = (level.timeMetrics.percentage100 + 1..level.timeMetrics.percentage90).random()
        val gameStatus = GameStatus.Process(
            time,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Assert
        assertEquals(90.0, result)
    }

    @Test
    fun `test when is in the range 80`() {
        //Arrange
        val time = (level.timeMetrics.percentage90 + 1..level.timeMetrics.percentage80).random()
        val gameStatus = GameStatus.Process(
            time,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Assert
        assertEquals(80.0, result)
    }

    @Test
    fun `test when is in the range 70`() {
        //Arrange
        val time = (level.timeMetrics.percentage80 + 1..level.timeMetrics.percentage70).random()
        val gameStatus = GameStatus.Process(
            time,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Assert
        assertEquals(70.0, result)
    }

    @Test
    fun `test when is in the range 60`() {
        //Arrange
        val time = (level.timeMetrics.percentage70 + 1..level.timeMetrics.percentage60).random()
        val gameStatus = GameStatus.Process(
            time,
            50
        )

        //Act
        val result = levelScore.getScore(level, gameStatus)

        //Assert
        assertEquals(60.0, result)
    }
}