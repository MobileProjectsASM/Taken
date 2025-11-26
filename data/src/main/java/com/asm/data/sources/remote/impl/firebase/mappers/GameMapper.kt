package com.asm.data.sources.remote.impl.firebase.mappers

import com.asm.data.sources.remote.impl.firebase.data.CellColor
import com.asm.data.sources.remote.impl.firebase.data.GameFireStore
import com.asm.data.sources.remote.impl.firebase.data.MetricsKeys
import com.asm.data.sources.remote.impl.firebase.data.MetricsStateKeys
import com.asm.data.sources.remote.impl.firebase.data.StatusKeys
import com.asm.domain.entities.Game
import com.asm.domain.entities.GameStatus
import com.asm.domain.entities.GameStatus.Companion.StatusNames
import com.asm.domain.entities.MetaDataMetrics
import com.asm.domain.entities.ParamMetrics
import com.asm.domain.entities.UnitTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class GameMapper @Inject constructor(
    val gson: Gson
) {

    fun gameSourceToDomain(gameFireStore: GameFireStore): Game {
        return Game(
            id = gameFireStore.gameId,
            order = gameFireStore.levelInfo.order,
            name = gameFireStore.levelInfo.name,
            image = gameFireStore.levelInfo.image,
            status = getStatus(gameFireStore.status),
            metaDataMetrics = getMetaDataMetrics(gameFireStore.levelInfo.metrics),
            solution = convertStringToMatrix(gameFireStore.levelInfo.response),
            theme = getTheme(gameFireStore.levelInfo.theme)
        )
    }

    private fun getStatus(status: Any): GameStatus {
        return when {
            status is String -> when (status.uppercase()) {
                StatusNames.LOCK -> GameStatus.Lock
                StatusNames.NEW -> GameStatus.New
                else -> throw Exception("Unknown status")
            }

            else -> {
                val id =
                    ((status as? Map<String, Any>)?.get(StatusKeys.STATUS_ID) as? String)?.uppercase()
                when (id) {
                    GameStatus.Companion.StatusNames.WIN -> GameStatus.Win(
                        paramMetrics = (status[StatusKeys.METRICS_STATE] as? Map<String, Any>)
                            ?.let(this::getParamMetrics) ?: throw Exception("Format data metrics incorrect"),
                        score = (status[StatusKeys.SCORE] as? Int) ?: 0
                    )


                    GameStatus.Companion.StatusNames.PROCESS -> GameStatus.Process(
                        paramMetrics = (status[StatusKeys.METRICS_STATE] as? Map<String, Any>)
                            ?.let(this::getParamMetrics) ?: throw Exception("Format data metrics incorrect"),
                        currentTable = (status[StatusKeys.TABLE_STATE] as? String)
                            ?.let(this::convertStringToMatrix) ?: listOf()
                    )

                    else -> throw Exception("Unknown status")
                }
            }
        }
    }

    private fun getParamMetrics(metrics: Map<String, Any>): ParamMetrics {
        return when (metrics[MetricsStateKeys.METRICS_ID]) {
            ParamMetrics.Companion.ParamType.TIME -> ParamMetrics.ParamTime(
                (metrics[MetricsStateKeys.TIME_PLAYED] as? Int) ?: 0
            )

            ParamMetrics.Companion.ParamType.MOVEMENTS -> ParamMetrics.ParamMovements(
                (metrics[MetricsStateKeys.MOVEMENTS_PLAYED] as? Int) ?: 0
            )

            else -> throw Exception("Unknown data metrics")
        }
    }

    private fun convertStringToMatrix(currentTable: String): List<List<Int>> {
        val type = object : TypeToken<List<List<Int>>>() {}.type
        return gson.fromJson(currentTable, type)
    }

    private fun convertStringToArray(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    private fun getMetaDataMetrics(metaDataMetrics: Map<String, Any>): MetaDataMetrics {
        return when ((metaDataMetrics[MetricsKeys.METRICS_ID] as? String)?.uppercase()) {
            MetaDataMetrics.Companion.MetricsType.TIME -> MetaDataMetrics.Time(
                ranges = metaDataMetrics[MetricsKeys.METRICS_TABULATOR] as? Map<Int, Int> ?: mapOf(),
                unitTime = UnitTime.valueOf(
                    (metaDataMetrics[MetricsKeys.UNIT_TIME] as? String ?: "").uppercase()
                )
            )

            MetaDataMetrics.Companion.MetricsType.MOVEMENTS -> MetaDataMetrics.Movements(
                ranges = metaDataMetrics[MetricsKeys.METRICS_TABULATOR] as? Map<Int, Int> ?: mapOf(),
                initTable = (metaDataMetrics[MetricsKeys.INIT_TABLE] as? String)
                    ?.let(this::convertStringToMatrix) ?: listOf()
            )

            else -> throw Exception("Unknown meta data metrics")
        }
    }

    private fun getTheme(theme: List<CellColor>): Map<String, List<Int>> {
        val result: MutableMap<String, List<Int>> = mutableMapOf()
        theme.forEach {
            result[it.color] = convertStringToArray(it.cells)
        }
        return result
    }
}