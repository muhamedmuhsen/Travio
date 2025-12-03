package com.example.domain.usecase.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.User
import com.example.domain.repository.login.AuthRepository

class SignupUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String, password: String, username: String, firstname: String, lastname: String
    ): Result<User, AppError> {
        /* TODO: validate input fields */
        return repository.signup(
            email = email,
            password = password,
            firstname = firstname,
            lastname = lastname,
            username = username,
        )
    }
}