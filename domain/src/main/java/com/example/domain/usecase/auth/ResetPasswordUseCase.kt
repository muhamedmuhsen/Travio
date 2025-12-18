package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.validators.ValidatePasswordUseCase
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(newPassword: String): Result<Unit, AppError> {
        if (!validatePasswordUseCase(newPassword)) {
            return Result.Error(AppError.Validation.WeakPassword)
        }
        return authRepository.resetPassword(newPassword)
    }
}