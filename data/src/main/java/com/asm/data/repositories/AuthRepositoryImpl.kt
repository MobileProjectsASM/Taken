package com.asm.data.repositories

import com.asm.data.sources.remote.abstract_remotes.AuthRemoteSource
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.ProviderId
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val authRemoteSource: AuthRemoteSource
): AuthRepository {

    companion object {
        const val TAG = "auth_repository"
    }

    override suspend fun authWithEmailAndPassword(
        email: String,
        password: String
    ): Result<AuthUser, GeneralError> {
        return try {
            authRemoteSource.authWithEmailAndPassword(email, password)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun authWithOAuth(
        token: String,
        providerId: String
    ): Result<AuthUser, GeneralError> {
        return try {
            authRemoteSource.authWithOAuth(token, providerId)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun authWithOpenIdConnect(
        token: String,
        providerId: String
    ): Result<AuthUser, GeneralError> {
        return try {
            authRemoteSource.authWithOtp(token, providerId)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun authWithOTP(
        sessionId: String,
        otp: String
    ): Result<AuthUser, GeneralError> {
        return try {
            authRemoteSource.authWithOtp(sessionId, otp)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun createAccount(
        email: String,
        password: String
    ): Result<Unit, GeneralError> {
        return try {
            authRemoteSource.createAccount(email, password)
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun getAuthUser(): Result<AuthUser, GeneralError> {
        return try {
            authRemoteSource.getAuthUser()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun signOut(): Result<Unit, GeneralError> {
        return try {
            authRemoteSource.signOut()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            return GeneralError.Unknown.toUnsuccessful()
        }
    }
}