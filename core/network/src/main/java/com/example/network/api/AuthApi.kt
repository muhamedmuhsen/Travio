package com.example.network.api

import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ForgetPasswordResponse
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordResponse
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeResponse
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.login.LoginResponse
import com.example.network.dto.auth.logout.LogoutResponse
import com.example.network.dto.auth.signup.SignupRequest
import com.example.network.dto.auth.signup.SignupResponse
import com.example.network.dto.auth.social.SocialLoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @POST("auth/social-login")
    suspend fun socialLogin(@Body request: SocialLoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse

    @POST("auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<LoginResponse>

    @POST("auth/forget-password")
    suspend fun forgetPassword(@Body request: ForgetPasswordRequest): ForgetPasswordResponse

    @POST("auth/verify-code")
    suspend fun sendVerificationCode(@Body request: VerificationCodeRequest): VerificationCodeResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse
}