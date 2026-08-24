package com.asm.domain.use_cases

import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.AuthenticationFailure
import com.asm.domain.errors.CommonFailure
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.SessionRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class SignInUserUC @Inject constructor(
    private val logger: Logger,
    private val gamerRepository: GamerRepository,
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) : UseCaseSync<Result<SignInUserUC.User, AuthenticationFailure>, SignInUserUC.CredentialType>() {

    companion object {
        const val TAG = "sign-in-user"
    }

    sealed class CredentialType {
        data class EmailAndPassword(
            val email: String,
            val password: String
        ) : CredentialType()

        data class Token(
            val token: String,
            val providerId: String
        ) : CredentialType()

        data class OTP(
            val sessionId: String,
            val otp: String
        ) : CredentialType()
    }

    sealed class User {
        data class RegisteredUser(val gamerId: String) : User()
        data class UnregisteredUser(val authUser: AuthUser) : User()
    }

    override suspend fun run(params: CredentialType): Result<User, AuthenticationFailure> {
        return try {
            val authUser = when (val authUserResult = authUser(params)) {
                is Result.Successful<AuthUser> -> authUserResult.data
                is Result.Unsuccessful<AuthenticationFailure> -> return authUserResult
            }

            val gamerExists = when (
                val gamerExistsResult = gamerRepository.verifyGamerExists(authUser.userId)
            ) {
                is Result.Successful<Boolean> -> gamerExistsResult.data
                is Result.Unsuccessful<AuthenticationFailure> -> return gamerExistsResult
            }

            val session = if (gamerExists) Session.UserRegister(authUser.userId)
            else Session.UserUnregister(
                userId = authUser.userId,
                userImage = authUser.profilePictureUrl
            )

            when (val saveSessionResult = sessionRepository.saveSession(session)) {
                is Result.Successful<Unit> -> saveSessionResult.data
                is Result.Unsuccessful<AuthenticationFailure> -> return saveSessionResult
            }

            val user = if (gamerExists) User.RegisteredUser(authUser.userId)
            else User.UnregisteredUser(authUser)

            Result.Successful(user)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }

    private suspend fun authUser(credentialType: CredentialType): Result<AuthUser, AuthenticationFailure> =
        when (credentialType) {
            is CredentialType.EmailAndPassword -> authRepository.authWithEmailAndPassword(
                credentialType.email,
                credentialType.password
            )

            is CredentialType.Token -> authRepository.authWithToken(
                credentialType.token,
                credentialType.providerId
            )

            is CredentialType.OTP -> authRepository.authWithOTP(
                credentialType.sessionId,
                credentialType.otp
            )
        }
}