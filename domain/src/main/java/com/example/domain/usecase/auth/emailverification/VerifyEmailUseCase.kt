package com.example.domain.usecase.auth.emailverification

import com.example.common.extensions.isValidOTP
import com.example.domain.repository.auth.EmailVerificationRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(private val repository: EmailVerificationRepository) {
    suspend operator fun invoke(email: String, otp: String): Result<Unit, DataError> {
        if (email.isBlank() || otp.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }

        if (!otp.isValidOTP()) {
            return Result.Error(DataError.Validation.InvalidOTPFormat)
        }

        return repository.verifyEmail(
            email = email,
            otp = otp
        )
    }

}