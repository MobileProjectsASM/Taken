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
}