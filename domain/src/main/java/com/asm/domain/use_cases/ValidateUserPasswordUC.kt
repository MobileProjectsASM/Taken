package com.asm.domain.use_cases

import com.asm.domain.entities.PasswordState
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class ValidateUserPasswordUC @Inject constructor(
    private val logger: Logger
) : UseCaseSync<Result<PasswordState, GeneralFailure>, String>() {
    override suspend fun run(params: String): Result<PasswordState, GeneralFailure> {
        /*return try {
            if (params.isEmpty()) return PasswordState.EMPTY.toSuccessful()
            if (!params.matches(
                Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")
            )) return PasswordState.INVALID_PATTERN.toSuccessful()
            PasswordState.VALID.toSuccessful()
        } catch (exception: Exception) {
            logger.logE { exception }
            Error.UnknownError.toFailure()
        }*/
        return Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
    }
}