package com.asm.domain.use_cases

import com.asm.domain.entities.Difficulty
import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.Level

class FakeGames {
    val moreThanOneGameInProcess = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 23.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.Process(67, 34),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
    )

    val moreThanOneNewGame = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 23.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        )
    )

    val moreThanOneLockGame = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 23.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.MEDIUM, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
    )

    val initialGames = listOf(
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        )
    )

    val firstLevelWin = listOf(
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        )
    )
    val multipleLevelsWin = listOf(
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
    val thereIsNotLevelLock = listOf(
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
    val allLevelsWon = listOf(
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
}

class GamesExpected {
    val expectedInitialGames = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        )
    )
    val firstLevelWinExpected = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        )
    )
    val multipleLevelsWinExpected = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Lock,
            arrayOf()
        )
    )
    val thereIsNotLevelLockExpected = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.New,
            arrayOf()
        )
    )
    val allLevelsWonExpected = listOf(
        Game(
            Level("", 1, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            Level("", 2, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            Level("", 3, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            Level("", 4, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            Level("", 5, "", Difficulty.EASY, 100, 1000, arrayOf()),
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        )
    )
}