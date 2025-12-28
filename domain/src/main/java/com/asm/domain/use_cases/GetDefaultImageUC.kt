package com.asm.domain.use_cases

import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetDefaultImageUC @Inject constructor(
    private val logger: Logger,
    private val multimediaRepository: MultimediaRepository
): UseCaseSync<Result<String?, GeneralError>, Unit>() {

    companion object {
        const val TAG = "Get Default Image Use Case"
    }

    override suspend fun run(params: Unit): Result<String?, GeneralError> {
        return try {
            multimediaRepository.getDefaultUserImage()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralError.Unknown)
        }
    }
}