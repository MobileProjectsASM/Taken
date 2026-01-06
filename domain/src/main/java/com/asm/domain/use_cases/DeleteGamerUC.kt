package com.asm.domain.use_cases

import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class DeleteGamerUC @Inject constructor(
    private val logger: Logger,
    private val gamerRepository: GamerRepository,
    private val multimediaRepository: MultimediaRepository,
    private val sessionRepository: SessionRepository
): UseCaseSync<Result<Unit, GeneralError>, DeleteGamerUC.DeleteGamerParams>() {

    companion object {
        const val TAG = "delete_gamer_use_case"
    }

    data class DeleteGamerParams(
        val gamerId: String,
        val signOutThirdProvider: suspend () -> Result<Unit, GeneralError>
    )

    override suspend fun run(params: DeleteGamerParams): Result<Unit, GeneralError> {
        return try {
            //Get default image
            val defaultImageResult = multimediaRepository.getDefaultUserImage()
            val defaultImage = when (defaultImageResult) {
                is Result.Successful<String?> -> defaultImageResult.data
                is Result.Unsuccessful<GeneralError> -> return defaultImageResult
            }

            //Get current gamer image
            val currentImage = when (val gamerResult = gamerRepository.getGamerById(params.gamerId)) {
                is Result.Successful<Gamer?> -> {
                    if (gamerResult.data == null) {
                        logger.logE(SaveChangesGamerUC.TAG, "Not found current gamer")
                        return GeneralError.Unknown.toUnsuccessful()
                    }
                    gamerResult.data.gamerImage
                }
                is Result.Unsuccessful<GeneralError> -> return gamerResult
            }

            //Delete image if it is not the default image
            if (currentImage != defaultImage) {
                val resultDelete = multimediaRepository.deleteResourceByUrl(currentImage)
                if (resultDelete is Result.Unsuccessful) return resultDelete
            }

            //Delete gamer
            val deleteGamerResult = gamerRepository.deleteGamer(params.gamerId)
            if (deleteGamerResult is Result.Unsuccessful) return deleteGamerResult

            //Sign out user third provider
            val signOutResult = params.signOutThirdProvider()
            if (signOutResult is Result.Unsuccessful) return signOutResult

            sessionRepository.closeSession()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}