package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<Unit, AppError> {
        if (code.isEmpty()) return Result.Error(AppError.Data.NotFound)
        if (code.length != 6) return Result.Error(AppError.Validation.MissingFields)
        return authRepository.sendVerificationCode(code)
    }
}