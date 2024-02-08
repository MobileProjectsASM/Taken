package com.asm.domain.entities

import com.asm.domain.entities.interfaces.MovementValidator

class RegularTableValidator : MovementValidator {
    override fun findCellEmpty(table: Array<Array<Box>>, row: Int, column: Int): Pair<Int, Int>? {
        if (row > 0) {
            val previousPiece = table[row - 1][column]
            if (previousPiece.number == 0) {
                return Pair(row - 1, column)
            }
        }
        if (row < table.size - 1) {
            val nextPiece = table[row + 1][column]
            if (nextPiece.number == 0) {
                return Pair(row + 1, column)
            }
        }
        if (column > 0) {
            val leftPiece = table[row][column - 1]
            if (leftPiece.number == 0) {
                return Pair(row, column - 1)
            }
        }
        if (column < table.size - 1) {
            val rightPiece = table[row][column + 1]
            if (rightPiece.number == 0) {
                return Pair(row, column + 1)
            }
        }
        return null
    }
}