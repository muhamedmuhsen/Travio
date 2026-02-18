package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.PasswordResetRepository
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import javax.inject.Inject

class PasswordResetRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenProvider: TokenProvider
) : PasswordResetRepository {

    override suspend fun forgetPassword(email: String): Result<Unit, DataError> = safeApiCall {
        api.forgetPassword(ForgetPasswordRequest(email))
    }

    override suspend fun sendVerificationCode(
        email: String,
        code: String
    ): Result<Unit, DataError> = safeApiCall {
        val response =
            api.sendVerificationCode(VerificationCodeRequest(email = email, otp = code))
        tokenProvider.saveResetToken(response.resetToken)
    }

    override suspend fun resetPassword(
        resetToken: String,
        email: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit, DataError> = safeApiCall {
        api.resetPassword(
            ResetPasswordRequest(
                resetToken,
                email,
                newPassword,
                confirmNewPassword
            )
        )
        handlePostResetPassword()
    }

    private suspend fun handlePostResetPassword() {
        tokenProvider.clearResetToken()
    }
}