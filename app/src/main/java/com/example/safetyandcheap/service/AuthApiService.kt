package com.example.safetyandcheap.service

import com.example.safetyandcheap.data.models.LoginRequest
import com.example.safetyandcheap.data.models.LoginResponse
import com.example.safetyandcheap.data.models.RegisterRequest
import com.example.safetyandcheap.data.models.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("auth/verify")
    suspend fun verify(@Body request: VerifyRequest): Response<LoginResponse>
}