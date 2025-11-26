package com.asm.data.sources.remote.impl.firebase.data

data class GameFireStore(
    val gameId: String,
    val levelInfo: LevelInfo,
    val status: Any
)

data class LevelInfo(
    val id: String,
    val image: String,
    val name: String,
    val order: Int,
    val response: String,
    val metrics: Map<String, Any>,
    val theme: List<CellColor>
)

data class CellColor(
    val color: String,
    val cells: String
)

object StatusKeys {
    const val STATUS_ID = "status_id"
    const val SCORE = "score"
    const val METRICS_STATE = "metrics_id"
    const val TABLE_STATE = "table_state"
}

object MetricsStateKeys {
    const val METRICS_ID = "id"
    const val TIME_PLAYED = "time_played"
    const val MOVEMENTS_PLAYED = "movements_played"
}

object MetricsKeys {
    const val METRICS_ID = "id"
    const val METRICS_TABULATOR = "tabulator"
    const val UNIT_TIME = "unit_time"
    const val INIT_TABLE = "init_table"
}
