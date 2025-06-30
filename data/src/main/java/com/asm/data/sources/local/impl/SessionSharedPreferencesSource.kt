package com.asm.data.sources.local.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.asm.data.sources.local.interfaces.SessionLocalSource
import com.asm.domain.entities.Session
import com.google.gson.Gson
import javax.inject.Inject

class SessionSharedPreferencesSource @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
): SessionLocalSource {

    companion object {
        const val SESSION_KEY = "session"
        const val TAG = "SessionSharedPreferencesSource"
    }

    override suspend fun fetchSession(): Session? {
        val data = sharedPreferences.getString(SESSION_KEY, "") ?: return null
        val session = gson.fromJson(data, Session::class.java)
        return session
    }

    override suspend fun saveSession(session: Session) {
        val data = gson.toJson(session, Session::class.java)
        sharedPreferences.edit(commit = true) {
            putString(SESSION_KEY, data)
        }
    }

    override suspend fun closeSession() {
        sharedPreferences.edit(commit = true) {
            remove(SESSION_KEY)
        }
    }
}