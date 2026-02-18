package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.EmailVerificationRepository
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.SendVerifyOTPRequest
import com.example.network.dto.auth.VerifyEmailRequest
import javax.inject.Inject

class EmailVerificationRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val preferencesManager: PreferencesManager
) : EmailVerificationRepository {
    override suspend fun sendVerifyEmailOtp(email: String): Result<String, DataError> =
        safeApiCall {
            val request = SendVerifyOTPRequest(email)
            val response = api.sendVerifyEmailOtp(request)
            return@safeApiCall response.expiresOn
        }

    override suspend fun verifyEmail(
        email: String, otp: String
    ): Result<Unit, DataError> = safeApiCall {
        val request = VerifyEmailRequest(email, otp)
        api.verifyEmail(request)

        preferencesManager.setLoggedIn(true)
    }
}