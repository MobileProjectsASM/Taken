package com.asm.domain.entities

class TableManagement {
    fun getInitTable(level: Level, initTable: Array<Array<Int>>): Array<Array<Box>> {
        val solutionMap = mutableMapOf<Int, Box>()
        level.response.forEach { row ->
            row.forEach { box ->
                solutionMap[box.number] = box
            }
        }

        return Array(initTable.size) { indexRow ->
            Array(initTable[indexRow].size) { indexColumn ->
                val value = initTable[indexRow][indexColumn]
                val color = solutionMap[value]?.color ?: 16711680
                Box(value, color)
            }
        }
    }
}