package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.errors.Failure
import com.asm.domain.errors.RegisterFailure
import com.asm.domain.repositories.GamerRepositories
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Completed
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toRight

class SignInUC(
    private val gamerRepositories: GamerRepositories,
    private val logger: Logger
) : UseCaseSync<Completed, Gamer>() {
    override suspend fun run(params: Gamer): Either<Failure, Completed> {
        try {
            val result = gamerRepositories.checkIfGamerExists(params.gamerId)
            if (result.isLeft) return result as Either.Left
            val gamerExists = (result as Either.Right).r
            if (gamerExists) return Either.Left(RegisterFailure.GamerExists)
            val resultRegisterGamer = gamerRepositories.registerGamer(params)
            if (resultRegisterGamer.isLeft) return resultRegisterGamer as Either.Left
            return Completed.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Either.Left(Failure.UnknownError)
        }
    }
}