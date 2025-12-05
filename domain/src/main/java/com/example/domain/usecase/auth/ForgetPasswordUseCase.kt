package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.validators.ValidateEmailUseCase

class ForgetPasswordUseCase constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, AppError> {
        if (!validateEmailUseCase(email)) {
            return Result.Error(AppError.Validation.InvalidEmailFormat)
        }
        return authRepository.forgetPassword(email)
    }
}