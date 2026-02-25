package com.example.domain.repository.auth

import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface SignupRepository {
    suspend fun signup(
        email: String,
        password: String,
        username: String,
        firstname: String,
        lastname: String,
        confirmPassword: String
    ): Result<Unit, DataError>
}
