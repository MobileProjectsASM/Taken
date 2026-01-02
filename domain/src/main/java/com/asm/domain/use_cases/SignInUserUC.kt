package com.asm.domain.use_cases

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger

class SignInUserUC(
    private val logger: Logger,
    private val gamerRepository: GamerRepository,
    private val sessionRepository: SessionRepository
) : UseCaseSync<Result<SignInUserUC.User, GeneralError>, SignInUserUC.CredentialType>() {

    companion object {
        const val TAG = "SignInUser"
    }

    sealed class CredentialType {
        data class EmailAndPassword(
            val email: String,
            val password: String,
            val authMethod: suspend (String, String) -> Result<AuthUser, GeneralError>
        ) : CredentialType()

        data class Token(
            val token: String,
            val authMethod: suspend (String) -> Result<AuthUser, GeneralError>
        ) : CredentialType()
    }

    sealed class User {
        data class RegisteredUser(val gamerId: String): User()
        data class UnregisteredUser(val authUser: AuthUser): User()
    }

    override suspend fun run(params: CredentialType): Result<User, GeneralError> {
        return try {
            when (val authUserResult = authUser(params)) {
                is Result.Successful<AuthUser> -> when (val gamerExistsResult =
                    gamerRepository.verifyGamerExists(authUserResult.data.userId)) {
                    is Result.Successful<Boolean> -> {
                        val session = when (gamerExistsResult.data) {
                            true -> Session.UserRegister(authUserResult.data.userId)
                            false -> Session.UserUnregister(
                                authUserResult.data.userId,
                                authUserResult.data.profilePictureUrl
                            )
                        }
                        when (val saveSessionResult = sessionRepository.saveSession(session)) {
                            is Result.Successful<Unit> -> {
                                val user = when (gamerExistsResult.data) {
                                    true ->  User.RegisteredUser(authUserResult.data.userId)
                                    false -> User.UnregisteredUser(authUserResult.data)
                                }
                                Result.Successful(user)
                            }

                            is Result.Unsuccessful<GeneralError> -> saveSessionResult
                        }
                    }

                    is Result.Unsuccessful<GeneralError> -> gamerExistsResult
                }

                is Result.Unsuccessful<GeneralError> -> authUserResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    private suspend fun authUser(credentialType: CredentialType): Result<AuthUser, GeneralError> =
        when (credentialType) {
            is CredentialType.EmailAndPassword -> credentialType.authMethod(
                credentialType.email,
                credentialType.password
            )

            is CredentialType.Token -> credentialType.authMethod(credentialType.token)
        }
}