package com.example.domain.usecase.auth

import com.example.domain.utils.Result
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import com.example.domain.validators.ValidateEmailUseCase
import javax.inject.Inject

class ForgetPasswordUseCase @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, DataError> {
        if (!validateEmailUseCase(email)) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }
        return authRepository.forgetPassword(email)
    }
}