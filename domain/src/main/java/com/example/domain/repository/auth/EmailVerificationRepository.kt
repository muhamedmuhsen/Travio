package com.example.domain.repository.auth

import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface EmailVerificationRepository {
    suspend fun sendVerifyEmailOtp(email: String): Result<String, DataError>
    suspend fun verifyEmail(email: String, otp: String): Result<Unit, DataError>
}