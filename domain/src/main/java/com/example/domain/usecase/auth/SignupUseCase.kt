package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.User
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.validators.ValidateEmailUseCase
import com.example.domain.validators.ValidatePasswordUseCase
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) {
    suspend operator fun invoke(
        email: String, password: String, username: String, firstname: String, lastname: String
    ): Result<User, AppError> {
        /* TODO: validate input fields */
        if (!validateEmailUseCase(email)) {
            return Result.Error(AppError.Validation.InvalidEmailFormat)
        }
        if (!validatePasswordUseCase(password)) {
            return Result.Error(AppError.Validation.WeakPassword)
        }
        if (firstname.isBlank() || lastname.isBlank() || username.isBlank()) {
            return Result.Error(AppError.Validation.InvalidInputs)
        }

        return repository.signup(
            email = email,
            password = password,
            firstname = firstname,
            lastname = lastname,
            username = username,
        )
    }
}