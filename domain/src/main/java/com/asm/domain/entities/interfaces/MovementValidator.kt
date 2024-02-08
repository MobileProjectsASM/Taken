package com.asm.domain.entities.interfaces

import com.asm.domain.entities.Box

interface MovementValidator {
    fun findCellEmpty(table: Array<Array<Box>>, row: Int, column: Int): Pair<Int, Int>?
}