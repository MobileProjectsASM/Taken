package com.asm.domain.entities

class SolutionValidator {
    fun validateSolution(level: Level, solution: Array<Array<Box>>): Boolean {
        var isSolutionValid = true
        for (indexRow in solution.indices) {
            val row = solution[indexRow]
            for (indexColumn in row.indices) {
                val boxSolution = solution[indexRow][indexColumn]
                val boxResponse = level.response[indexRow][indexColumn]
                if (boxSolution.number != boxResponse.number) {
                    isSolutionValid = false
                    break
                }
            }
            if (!isSolutionValid) break
        }
        return isSolutionValid
    }
}