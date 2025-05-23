package com.example.safetyandcheap.data.network.incerceptor

import com.example.safetyandcheap.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BearerInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getTokenSync()
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}