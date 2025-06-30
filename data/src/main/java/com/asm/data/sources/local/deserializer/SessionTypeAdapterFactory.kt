package com.asm.data.sources.local.deserializer

import com.asm.domain.entities.Session
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import javax.inject.Inject

class SessionTypeAdapterFactory @Inject constructor(): TypeAdapterFactory {

    companion object {
        const val TAG = "SessionSerializer"
        const val SESSION_TYPE = "sessionType"
        const val SESSION_OBJECT = "sessionObject"
        const val TYPE_REGISTER = "typeRegister"
        const val TYPE_UNREGISTER = "typeUnregister"
    }

    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        type ?: throw Exception("TypeToken is null")
        gson ?: throw Exception("Gson is null")
        if (type.rawType != Session::class.java) return null

        val userUnregisterAdapter = gson.getDelegateAdapter(this, TypeToken.get(Session.UserUnregister::class.java))
        val userRegisterAdapter = gson.getDelegateAdapter(this, TypeToken.get(Session.UserRegister::class.java))

        return object : TypeAdapter<Session>() {

            @Throws(IOException::class)
            override fun write(out: JsonWriter?, value: Session?) {
                out ?: throw Exception("JsonWriter is null")
                if (value == null) {
                    out.nullValue()
                    return
                }

                out.beginObject()
                when (value) {
                    is Session.UserRegister -> {
                        out.name(SESSION_TYPE).value(TYPE_REGISTER)
                        out.name(SESSION_OBJECT)
                        userRegisterAdapter.write(out, value)
                    }
                    is Session.UserUnregister -> {
                        out.name(SESSION_TYPE).value(TYPE_UNREGISTER)
                        out.name(SESSION_OBJECT)
                        userUnregisterAdapter.write(out, value)
                    }
                }
                out.endObject()
            }

            override fun read(input: JsonReader?): Session {
                val jsonObject = input?.let { JsonParser.parseReader(it).asJsonObject } ?: throw Exception("JsonReader is null")
                val sessionType = jsonObject.get(SessionSerializer.SESSION_TYPE)?.asString ?: throw Exception("Session type isn't defined")
                val sessionObject = jsonObject.get(SessionSerializer.SESSION_OBJECT) ?: throw Exception("Session object isn't defined")
                return when (sessionType) {
                    SessionSerializer.TYPE_REGISTER -> userRegisterAdapter.fromJsonTree(sessionObject)
                    SessionSerializer.TYPE_UNREGISTER -> userUnregisterAdapter.fromJsonTree(sessionObject)
                    else -> throw Exception("type not founded")
                }
            }
        }.nullSafe() as TypeAdapter<T>
    }
}