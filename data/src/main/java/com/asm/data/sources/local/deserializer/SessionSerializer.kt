package com.asm.data.sources.local.deserializer

import com.asm.domain.entities.Session
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import javax.inject.Inject

class SessionSerializer @Inject constructor(): JsonDeserializer<Session>, JsonSerializer<Session> {

    companion object {
        const val TAG = "SessionSerializer"
        const val SESSION_TYPE = "sessionType"
        const val SESSION_OBJECT = "sessionObject"
        const val TYPE_REGISTER = "typeRegister"
        const val TYPE_UNREGISTER = "typeUnregister"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Session {
        context ?: throw Exception("JsonSerializationContext is null")
        val jsonObject = json?.asJsonObject ?: throw Exception("Json is not valid")
        val sessionType = jsonObject.get(SESSION_TYPE)?.asString ?: throw Exception("Session type isn't defined")
        val sessionObject = jsonObject.get(SESSION_OBJECT) ?: throw Exception("Session object isn't defined")
        return when (sessionType) {
            TYPE_REGISTER -> {
                context.deserialize<Session.UserRegister>(sessionObject, Session.UserRegister::class.java)
            }
            TYPE_UNREGISTER -> {
                context.deserialize<Session.UserUnregister>(sessionObject, Session.UserUnregister::class.java)
            }
            else -> throw Exception("type not founded")
        }
    }

    override fun serialize(
        src: Session?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        context ?: throw Exception("JsonSerializationContext is null")
        val session = src ?: throw Exception("Session is null")
        val jsonObject = JsonObject()
        when (session) {
            is Session.UserRegister -> {
                jsonObject.addProperty(SESSION_TYPE, TYPE_REGISTER)
                jsonObject.addProperty(SESSION_OBJECT, session.gamerId)
            }

            is Session.UserUnregister -> {
                jsonObject.addProperty(SESSION_TYPE, TYPE_UNREGISTER)
                val content = context.serialize(session)
                jsonObject.add(SESSION_OBJECT, content)
            }
        }
        return jsonObject
    }
}