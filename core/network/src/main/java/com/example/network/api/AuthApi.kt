package com.example.network.api

import com.example.network.dto.auth.LoginRequest
import com.example.network.dto.auth.LoginResponse
import com.example.network.dto.auth.LogoutResponse
import com.example.network.dto.auth.SignupRequest
import com.example.network.dto.auth.SignupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse
}