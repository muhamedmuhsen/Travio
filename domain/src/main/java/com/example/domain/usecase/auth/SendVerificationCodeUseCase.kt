package com.example.domain.usecase.auth

import com.example.common.extensions.isValidOTP
import com.example.domain.utils.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): Result<Unit, DataError> {
        if (email.isBlank()) return Result.Error(DataError.Validation.MissingFields)
        if (!code.isValidOTP()) return Result.Error(DataError.Validation.InvalidOTPFormat)
        return authRepository.sendVerificationCode(email, code)
    }
}