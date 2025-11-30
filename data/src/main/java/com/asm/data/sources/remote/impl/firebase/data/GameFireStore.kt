package com.asm.data.sources.remote.impl.firebase.data

data class GameFireStore(
    val gameId: String = "",
    val levelInfo: LevelInfo = LevelInfo(),
    val gameStatus: Any = ""
)

data class LevelInfo(
    val levelId: String = "",
    val levelImage: String = "",
    val levelName: Map<String, String> = mapOf(),
    val levelOrder: Int = 0,
    val response: String = "",
    val metrics: Map<String, Any> = mapOf(),
    val theme: List<CellColor> = listOf()
)

data class CellColor(
    val color: String = "",
    val numbers: String = ""
)

object StatusKeys {
    const val STATUS_ID = "statusId"
    const val SCORE = "score"
    const val METRICS_STATE = "metricsId"
    const val TABLE_STATE = "tableState"
}

object MetricsStateKeys {
    const val METRICS_ID = "id"
    const val TIME_PLAYED = "timePlayed"
    const val MOVEMENTS_PLAYED = "movementsPlayed"
}

object MetricsKeys {
    const val METRICS_ID = "id"
    const val METRICS_TABULATOR = "tabulator"
    const val UNIT_TIME = "unit_time"
    const val INIT_TABLE = "initTable"
}

enum class Language(val code: String) {
    SPANISH("es"),
    ENGLISH("en")
}