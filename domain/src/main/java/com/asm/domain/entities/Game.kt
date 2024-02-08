package com.asm.domain.entities

data class Game(
    val level: Level,
    val gameStatus: GameStatus,
    val workTable: Array<Array<Box>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (level != other.level) return false
        if (gameStatus != other.gameStatus) return false
        return workTable.contentDeepEquals(other.workTable)
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + gameStatus.hashCode()
        result = 31 * result + workTable.contentDeepHashCode()
        return result
    }
}