package com.example.domain.usecase.auth.signup

import com.example.common.extensions.isValidEmail
import com.example.common.extensions.isValidName
import com.example.domain.repository.auth.SignupRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: SignupRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        firstname: String,
        lastname: String
    ): Result<Unit, DataError> {
        if (firstname.isBlank() || lastname.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.Error(DataError.Validation.MissingFields)
        }
        if (!firstname.isValidName()) {
            return Result.Error(DataError.Validation.ShortName)
        }
        if (!lastname.isValidName())
            return Result.Error(DataError.Validation.ShortName)

        if (!email.isValidEmail()) {
            return Result.Error(DataError.Validation.InvalidEmailFormat)
        }
        if (!password.isValidEmail()) {
            return Result.Error(DataError.Validation.WeakPassword)
        }

        return repository.signup(
            email = email,
            password = password,
            firstname = firstname,
            lastname = lastname,
            username = username,
            confirmPassword = password
        )

    }
}