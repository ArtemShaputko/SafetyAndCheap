package com.example.safetyandcheap.data.network.incerceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class ImgurAuthInterceptor @Inject constructor(
    @Named("imgur_client_id") private val clientId: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Client-ID $clientId")
            .build()
        return chain.proceed(request)
    }
}