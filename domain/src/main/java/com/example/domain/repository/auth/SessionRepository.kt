package com.example.domain.repository.auth

import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface SessionRepository {
    suspend fun logout(): Result<Unit, DataError>
    suspend fun isAuthenticated(): Result<Boolean, DataError>
    suspend fun refreshToken(): Result<Unit, DataError>
}
