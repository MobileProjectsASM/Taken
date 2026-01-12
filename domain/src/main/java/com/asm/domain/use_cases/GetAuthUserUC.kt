package com.asm.domain.use_cases

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetAuthUserUC @Inject constructor(
    private val logger: Logger,
    private val authRepository: AuthRepository
): UseCaseSync<Result<AuthUser, GeneralError>, Unit>() {

    companion object {
        const val TAG = "get_authenticated_user_use_case"
    }

    override suspend fun run(params: Unit): Result<AuthUser, GeneralError> {
        return try {
            authRepository.getAuthUser()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}