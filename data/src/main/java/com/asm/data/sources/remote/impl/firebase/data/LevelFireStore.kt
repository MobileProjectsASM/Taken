package com.asm.data.sources.remote.impl.firebase.data

data class LevelFireStore(
    val levelId: String,
    val levelName: String,
    val orderCriteria: Int,
    val levelImage: String,
    val difficulty: String,
    val timeMetrics: Map<String, Int>,
    val movementsMetrics: Map<String, Int>,
    val response: Array<Array<Map<Int, Long>>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LevelFireStore

        if (levelId != other.levelId) return false
        if (levelName != other.levelName) return false
        if (orderCriteria != other.orderCriteria) return false
        if (levelImage != other.levelImage) return false
        if (difficulty != other.difficulty) return false
        if (timeMetrics != other.timeMetrics) return false
        if (movementsMetrics != other.movementsMetrics) return false
        if (!response.contentDeepEquals(other.response)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = levelId.hashCode()
        result = 31 * result + levelName.hashCode()
        result = 31 * result + orderCriteria
        result = 31 * result + levelImage.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + timeMetrics.hashCode()
        result = 31 * result + movementsMetrics.hashCode()
        result = 31 * result + response.contentDeepHashCode()
        return result
    }
}