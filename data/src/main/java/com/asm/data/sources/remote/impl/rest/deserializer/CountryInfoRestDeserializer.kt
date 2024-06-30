package com.asm.data.sources.remote.impl.rest.deserializer

import android.util.Log
import com.asm.data.sources.remote.model.CountryInfoRest
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject

class CountryInfoDeserializer @Inject constructor(): JsonDeserializer<List<CountryInfoRest>> {
    companion object {
        const val TAG = "CountryInfoRestDeserializer"
        const val DATA = "data"
        const val COUNTRY_NAME = "name"
        const val ISO_3 = "iso3"
        const val CALL_CODE = "phone_code"
        const val H_REF = "href"
        const val FLAG = "flag"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<CountryInfoRest> {
        return try {
            val jObject = json?.asJsonObject ?: throw Exception("json is null")
            val djArray = jObject.getAsJsonArray(DATA) ?: throw Exception("Not exists member")
            djArray.map { countryElement ->
                val countryObject = countryElement?.asJsonObject
                val name = countryObject?.get(COUNTRY_NAME)?.asString ?: ""
                val iso3 = countryObject?.get(ISO_3)?.asString ?: ""
                val callCode = countryObject?.get(CALL_CODE)?.asString ?: ""
                val hrefJObject = countryObject?.getAsJsonObject(H_REF)
                val flag = hrefJObject?.get(FLAG)?.asString ?: ""
                CountryInfoRest(
                    countryName = name,
                    iso3 = iso3,
                    callCode = callCode,
                    flag = flag
                )
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw Exception("Error to deserialize object")
        }
    }
}