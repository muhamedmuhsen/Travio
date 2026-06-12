package com.example.domain.repository.auth

import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface LoginRepository {
    suspend fun login(
        email: String,
        password: String,
        isRememberMeChecked: Boolean
    ): Result<Unit, DataError>

    suspend fun signInWithGoogle(idToken: String): Result<Unit, DataError>
}
