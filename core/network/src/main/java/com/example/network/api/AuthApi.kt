package com.example.network.api

import com.example.network.dto.auth.LoginRequest
import com.example.network.dto.auth.LoginResponse
import com.example.network.dto.auth.LogoutResponse
import com.example.network.dto.auth.SignupRequest
import com.example.network.dto.auth.SignupResponse
import com.example.network.dto.auth.SocialLoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("auth/social-login")
    suspend fun socialSignin(@Body request: SocialLoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse

    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<LoginResponse>
}