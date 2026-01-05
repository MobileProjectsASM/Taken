package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class CreateAccountUC @Inject constructor(
    private val logger: Logger,
    private val authRepository: AuthRepository
): UseCaseSync<Result<Unit, GeneralError>, CreateAccountUC.CreateAccountParams>() {

    companion object {
        const val TAG = "create_account_use_case"
    }

    data class CreateAccountParams(
        val email: String,
        val password: String
    )

    override suspend fun run(params: CreateAccountParams): Result<Unit, GeneralError> {
        return try {
            authRepository.createAccount(params.email, params.password)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}