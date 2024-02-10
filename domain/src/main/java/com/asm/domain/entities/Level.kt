package com.asm.domain.entities

data class Level(
    val levelName: String,
    val levelOrder: Int,
    val levelImage: String,
    val difficulty: Difficulty,
    val maxTime: Int,
    val minMovements: Int,
    val response: Array<Array<Box>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Level

        if (levelName != other.levelName) return false
        if (levelOrder != other.levelOrder) return false
        if (levelImage != other.levelImage) return false
        if (difficulty != other.difficulty) return false
        if (maxTime != other.maxTime) return false
        if (minMovements != other.minMovements) return false
        return response.contentDeepEquals(other.response)
    }

    override fun hashCode(): Int {
        var result = levelName.hashCode()
        result = 31 * result + levelOrder
        result = 31 * result + levelImage.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + maxTime
        result = 31 * result + minMovements
        result = 31 * result + response.contentDeepHashCode()
        return result
    }
}