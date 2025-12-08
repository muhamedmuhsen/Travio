package com.example.domain.repository.auth

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.User
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User, AppError>
    suspend fun signup(
        email: String, password: String, username: String, firstname: String, lastname: String
    ): Result<User, AppError>

    suspend fun signInWithGoogle(idToken: String): Result<User, AppError>
    suspend fun signInWithFacebook(accessToken: String): Result<String, AppError>
    suspend fun logout(): Result<Unit, AppError>
    suspend fun isAuthenticated(): Result<Boolean, AppError>

    suspend fun refreshToken(): Result<Unit, AppError>

    suspend fun forgetPassword(email: String): Result<Unit, AppError>
    suspend fun sendVerificationCode(code: String): Result<Unit, AppError>
    suspend fun resetPassword(newPassword: String): Result<Unit, AppError>

}