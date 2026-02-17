package com.example.domain.repository.auth

import com.example.domain.utils.Result
import com.example.domain.utils.DataError

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
        isRememberMeChecked: Boolean
    ): Result<Unit, DataError>

    suspend fun signup(
        email: String,
        password: String,
        username: String,
        firstname: String,
        lastname: String,
        confirmPassword: String
    ): Result<Unit, DataError>

    suspend fun signInWithGoogle(idToken: String): Result<Unit, DataError>
    suspend fun signInWithFacebook(accessToken: String): Result<Unit, DataError>
    suspend fun logout(): Result<Unit, DataError>
    suspend fun verifyEmail(): Result<Unit, DataError>
    suspend fun isAuthenticated(): Result<Boolean, DataError>

    suspend fun refreshToken(): Result<Unit, DataError>

    suspend fun forgetPassword(email: String): Result<Unit, DataError>
    suspend fun sendVerificationCode(email: String, code: String): Result<Unit, DataError>
    suspend fun resetPassword(
        resetToken: String,
        email: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit, DataError>

}