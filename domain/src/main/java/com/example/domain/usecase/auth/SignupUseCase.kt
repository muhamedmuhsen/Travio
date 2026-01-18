package com.example.domain.usecase.auth

import com.example.domain.utils.Result
import com.example.domain.model.User
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.utils.DataError
import com.example.domain.validators.ValidateEmailUseCase
import com.example.domain.validators.ValidatePasswordUseCase
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        firstname: String,
        lastname: String
    ): Result<Unit, DataError> {
        if (firstname.isBlank() || lastname.isBlank() || username.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }
        if (!validateEmailUseCase(email)) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }
        if (!validatePasswordUseCase(password)) {
            return Result.Error(DataError.Validation.WeakPassword)
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