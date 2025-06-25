package com.asm.data.sources.local.deserializer

import com.asm.domain.entities.Session
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import javax.inject.Inject

class SessionSerializer @Inject constructor(): JsonDeserializer<Session>, JsonSerializer<Session> {

    companion object {
        const val TAG = "SessionSerializer"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Session {
        TODO("Not yet implemented")
    }

    override fun serialize(
        src: Session?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        TODO("Not yet implemented")
    }
}