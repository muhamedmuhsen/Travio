package com.example.domain.usecase.auth.passwordreset

import com.example.common.extensions.isValidOTP
import com.example.domain.repository.auth.PasswordResetRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authRepository: PasswordResetRepository
) {
    suspend operator fun invoke(
        email: String,
        code: String
    ): Result<Unit, DataError> {
        if (code.isBlank()) return Result.Error(DataError.Validation.MissingFields)
        if (!code.isValidOTP()) return Result.Error(DataError.Validation.InvalidOTPFormat)
        return authRepository.sendVerificationCode(email, code)
    }
}
