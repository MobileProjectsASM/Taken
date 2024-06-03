package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.LevelInfo

class FakeGames {
    /*val moreThanOneGameInProcess = listOf(
        Game(
            "",
            LevelInfo("level_1", "",""),//
            GameStatus.Win(30, 20, 23.0),
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//),
            GameStatus.Process(67, 34),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Lock,
            arrayOf()
        ),
    )

    val moreThanOneNewGame = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 23.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//),
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Lock,
            arrayOf()
        )
    )

    val moreThanOneLockGame = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 23.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(35, 15, 25.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//),
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//),
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Lock,
            arrayOf()
        ),
    )

    val initialGames = listOf(
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.New,
            arrayOf()
        )
    )

    val firstLevelWin = listOf(
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        )
    )
    val multipleLevelsWin = listOf(
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.Lock,
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
    val thereIsNotLevelLock = listOf(
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
    val allLevelsWon = listOf(
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 78.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 76.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 85.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 86.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 67.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 84.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 72.0),
            arrayOf()
        ),
    )
}

class GamesExpected {
    val expectedInitialGames = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Lock,
            arrayOf()
        )
    )
    val firstLevelWinExpected = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 69.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Lock,
            arrayOf()
        )
    )
    val multipleLevelsWinExpected = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.New,
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.Lock,
            arrayOf()
        )
    )
    val thereIsNotLevelLockExpected = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.New,
            arrayOf()
        )
    )
    val allLevelsWonExpected = listOf(
        Game(
            LevelInfo(1, "",""),//
            GameStatus.Win(30, 20, 93.0),
            arrayOf()
        ),
        Game(
            LevelInfo(2, "",""),//
            GameStatus.Win(45, 30, 98.0),
            arrayOf()
        ),
        Game(
            LevelInfo(3, "",""),//
            GameStatus.Win(50, 35, 90.0),
            arrayOf()
        ),
        Game(
            LevelInfo(4, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        ),
        Game(
            LevelInfo(5, "",""),//
            GameStatus.Win(30, 20, 60.0),
            arrayOf()
        )
    )*/
}