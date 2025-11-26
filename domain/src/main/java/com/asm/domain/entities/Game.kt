package com.asm.domain.entities

data class Game(
    val id: String,
    val order: Int,
    val name: String,
    val image: String,
    val status: GameStatus,
    val metaDataMetrics: MetaDataMetrics,
    val solution: List<List<Int>>,
    val theme: Map<String, List<Int>>
)

sealed class GameStatus {

    companion object {
        object StatusNames {
            const val NEW = "NEW"
            const val LOCK = "LOCK"
            const val PROCESS = "PROCESS"
            const val WIN = "WIN"
        }
    }

    data object Lock: GameStatus()
    data object New: GameStatus()
    data class Process(
        val currentTable: List<List<Int>>,
        val paramMetrics: ParamMetrics
    ): GameStatus()
    data class Win(
        val paramMetrics: ParamMetrics,
        val score: Int
    ): GameStatus()
}

sealed class ParamMetrics {

    companion object {
        object ParamType {
            const val TIME = "TIME"
            const val MOVEMENTS = "MOVEMENTS"
        }
    }

    data class ParamTime(
        private val timePlayed: Int
    ): ParamMetrics()
    data class ParamMovements(
        private val movementsPlayed: Int
    ): ParamMetrics()
}

sealed class MetaDataMetrics {
    companion object {
        object MetricsType {
            const val TIME = "TIME"
            const val MOVEMENTS = "MOVEMENTS"
        }
    }

    data class Time(
        private val ranges: Map<Int, Int>,
        private val unitTime: UnitTime
    ): MetaDataMetrics()
    data class Movements(
        private val ranges: Map<Int, Int>,
        private val initTable: List<List<Int>>
    ): MetaDataMetrics()
}

enum class UnitTime {
    SECONDS, MINUTES
}
