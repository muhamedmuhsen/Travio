package com.example.domain.repository.auth

import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface PasswordResetRepository {
    suspend fun forgetPassword(email: String): Result<Unit, DataError>
    suspend fun sendVerificationCode(
        email: String,
        code: String
    ): Result<Unit, DataError>
    suspend fun resetPassword(
        resetToken: String,
        email: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit, DataError>
}
