package com.example.domain.usecase.auth.login

import com.example.common.extensions.isValidEmail
import com.example.domain.repository.auth.LoginRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        isRememberMeChecked: Boolean
    ): Result<Unit, DataError> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }

        if (!email.isValidEmail()) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }

        return repository.login(email, password, isRememberMeChecked)
    }
}
