package com.asm.data.sources.remote.impl.rest.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class CountryInfoInterceptor @Inject constructor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val request = original.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", apiKey)
            .method(original.method(), original.body())
            .build()
        return chain.proceed(request)
    }
}