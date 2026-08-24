package com.asm.data.repositories

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.remote.abstract_remotes.AuthRemoteSource
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.AuthenticationFailure
import com.asm.domain.errors.CommonFailure
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.utils.Logger
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val authRemoteSource: AuthRemoteSource,
    private val connectionSource: ConnectionSource,
) : AuthRepository {

    companion object {
        const val TAG = "auth-repository"
    }

    override suspend fun authWithEmailAndPassword(
        email: String,
        password: String
    ): Result<AuthUser, AuthenticationFailure> {
        return try {
            connectionSource.ifConnectionIsAvailableRun {
                authRemoteSource.authWithEmailAndPassword(email, password)
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }

    override suspend fun authWithToken(
        token: String,
        providerId: String
    ): Result<AuthUser, AuthenticationFailure> {
        return try {
            connectionSource.ifConnectionIsAvailableRun {
                authRemoteSource.authWithToken(
                    token,
                    providerId
                )
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
        }
    }

    override suspend fun authWithOTP(
        sessionId: String,
        otp: String
    ): Result<AuthUser, AuthenticationFailure> {
        return try {
            connectionSource.ifConnectionIsAvailableRun {
                authRemoteSource.authWithOtp(
                    sessionId,
                    otp
                )
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(CommonFailure.UNEXPECTED_FAILURE)
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

    private suspend fun <T> ConnectionSource.ifConnectionIsAvailableRun(execute: suspend () -> Result<T, AuthenticationFailure>): Result<T, AuthenticationFailure> {
        return if (isNetworkAvailable()) execute()
        else Result.Unsuccessful(CommonFailure.NETWORK_CONNECTION)
    }
}