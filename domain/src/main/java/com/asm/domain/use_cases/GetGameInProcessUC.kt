package com.asm.domain.use_cases

import com.asm.domain.entities.Game
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.GameRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetGameInProcessUC @Inject constructor(
    private val logger: Logger,
    private val gameRepository: GameRepository
) : UseCaseSync<Result<Game, GeneralFailure>, String>() {
    override suspend fun run(params: String): Result<Game, GeneralFailure> {
        //TODO: implement getGameInProcess
        /*return try {
            val result = gameRepository.getGamerGameInProcess(params)
            if (result.isFailure) return result.asFailure().toFailure()
            result.asSuccessful().data?.toSuccessful() ?: GameError.ThereIsNotGameInProcess.toFailure()
        } catch (exception: Exception) {
            logger.logE { exception }
            Error.UnknownError.toFailure()
        }*/
        return Result.Unsuccessful(GeneralFailure.Unknown)
    }
}