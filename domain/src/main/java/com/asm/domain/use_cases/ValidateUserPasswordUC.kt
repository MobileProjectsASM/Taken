package com.asm.domain.use_cases

import com.asm.domain.entities.PasswordState
import com.asm.domain.errors.Failure
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Either
import com.asm.domain.utils.Logger
import com.asm.domain.utils.toLeft
import com.asm.domain.utils.toRight
import javax.inject.Inject

class ValidateUserPasswordUC @Inject constructor(
    private val logger: Logger
) : UseCaseSync<PasswordState, String>() {
    override suspend fun run(password: String): Either<Failure, PasswordState> {
        try {
            if (password.isEmpty()) return PasswordState.EMPTY.toRight()
            if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$"))) return PasswordState.INVALID_PATTERN.toRight()
            return PasswordState.VALID.toRight()
        } catch (exception: Exception) {
            logger.logE { exception }
            return Failure.UnknownError.toLeft()
        }
    }
}