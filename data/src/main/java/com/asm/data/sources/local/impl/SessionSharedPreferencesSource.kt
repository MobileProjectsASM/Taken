package com.asm.data.sources.local.impl

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.asm.data.sources.local.interfaces.SessionLocalSource
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.errors.toUnsuccessful
import com.google.gson.Gson
import javax.inject.Inject

class SessionSharedPreferencesSource @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : SessionLocalSource {

    companion object {
        const val SESSION_KEY = "session"
        const val TAG = "SessionSharedPreferencesSource"
    }

    override suspend fun fetchSession(): Result<Session?, GeneralError> {
        return try {
            sharedPreferences.getString(SESSION_KEY, null)?.takeIf { it.isNotBlank() }
                ?.let { data ->
                    gson.fromJson(data, Session::class.java).let {
                        Result.Successful(it)
                    }
                } ?: Result.Successful(null)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun saveSession(session: Session): Result<Unit, GeneralError> {
        return try {
            val data = gson.toJson(session, Session::class.java)
            sharedPreferences.edit(commit = true) {
                putString(SESSION_KEY, data)
            }
            Result.Successful(Unit)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }

    override suspend fun closeSession(): Result<Unit, GeneralError> {
        return try {
            sharedPreferences.edit(commit = true) {
                remove(SESSION_KEY)
            }
            Result.Successful(Unit)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            GeneralError.Unknown.toUnsuccessful()
        }
    }
}