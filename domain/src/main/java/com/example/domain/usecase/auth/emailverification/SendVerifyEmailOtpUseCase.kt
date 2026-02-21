package com.example.domain.usecase.auth.emailverification

import com.example.domain.repository.auth.EmailVerificationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SendVerifyEmailOtpUseCase @Inject constructor(private val repository: EmailVerificationRepository) {
    suspend operator fun invoke(email: String): Result<String, DataError> {
        return repository.sendVerifyEmailOtp(email)
    }
}