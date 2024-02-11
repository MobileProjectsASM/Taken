package com.asm.domain.entities

data class Game(
    val levelInfo: LevelInfo,
    val gameStatus: GameStatus,
    val workTable: Array<Array<Box>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (levelInfo != other.levelInfo) return false
        if (gameStatus != other.gameStatus) return false
        return workTable.contentDeepEquals(other.workTable)
    }

    override fun hashCode(): Int {
        var result = levelInfo.hashCode()
        result = 31 * result + gameStatus.hashCode()
        result = 31 * result + workTable.contentDeepHashCode()
        return result
    }
}