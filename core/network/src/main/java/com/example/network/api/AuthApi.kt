package com.example.network.api

import com.example.network.dto.auth.AuthApiResponseDto
import com.example.network.dto.auth.GoogleLoginRequest
import com.example.network.dto.auth.SendOtpResponseDto
import com.example.network.dto.auth.SendVerifyOTPRequest
import com.example.network.dto.auth.VerifyEmailRequest
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeResponse
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.logout.LogoutRequest
import com.example.network.dto.auth.signup.SignupRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("Auth/login")
    suspend fun login(@Body request: LoginRequest): AuthApiResponseDto

    @POST("Auth/register")
    suspend fun signup(@Body request: SignupRequest): AuthApiResponseDto

    @POST("Auth/google-login")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): AuthApiResponseDto

    @POST("Auth/Logout")
    suspend fun logout(@Body request: LogoutRequest)

    @POST("Auth/refreshToken")
    fun refreshToken(@Body refreshToken: String): Call<AuthApiResponseDto>

    @POST("Auth/forgot-password")
    suspend fun forgetPassword(@Body request: ForgetPasswordRequest)

    @POST("Auth/verify-reset-password-otp")
    suspend fun sendVerificationCode(@Body request: VerificationCodeRequest): VerificationCodeResponse

    @POST("Auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): AuthApiResponseDto

    @POST("Auth/send-verify-email-otp")
    suspend fun sendVerifyEmailOtp(@Body request: SendVerifyOTPRequest): SendOtpResponseDto

    @POST("Auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): AuthApiResponseDto
}