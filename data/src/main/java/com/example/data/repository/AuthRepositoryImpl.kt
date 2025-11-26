package com.example.data.repository

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.data.helpers.safeApiCall
import com.example.data.local.datastore.DataStoreManager
import com.example.data.mapper.toDomain
import com.example.domain.model.User
import com.example.domain.repository.Auth.TokenManager
import com.example.domain.repository.login.AuthRepository
import com.example.network.api.AuthApi
import com.example.network.dto.auth.LoginRequest
import com.example.network.dto.auth.SignupRequest

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val dataStoreManager: DataStoreManager,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User, AppError> {
        return safeApiCall {
            val response = api.login(LoginRequest(email, password))

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
            }
            dataStoreManager.saveToken(response.user.token)
            dataStoreManager.setLoggedIn(true)

            val user = response.user.toDomain(/* TODO: mapping to domain model */)

            Result.Success(user)
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        username: String,

        ): Result<User, AppError> {
        return safeApiCall {
            val response = api.signup(
                SignupRequest(
                    email = email, username = username, password = password
                )
            )
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
            }
            dataStoreManager.saveToken(response.user.token)
            dataStoreManager.setLoggedIn(true)

            val user = response.user.toDomain(
                //email, phone
            )
            Result.Success(user)
        }
    }

    override suspend fun logout(): Result<Unit, AppError> {
        return safeApiCall {
            val response = api.logout()
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Unknown())
            }

            dataStoreManager.removeToken()
            dataStoreManager.setLoggedIn(false)
            dataStoreManager.clearCredentials()

            Result.Success(Unit)
        }
    }

    override suspend fun isAuthenticated(): Result<Boolean, AppError> {
        val token = dataStoreManager.getToken()

        if (token.isNullOrEmpty()) {
            return Result.Success(false)
        }
        return safeApiCall {
            val token = dataStoreManager.getToken()
            if (token.isNullOrEmpty()) {
                return@safeApiCall Result.Success(false)
            }

            return@safeApiCall when (val result = tokenManager.isTokenExpired()) {
                is Result.Error -> Result.Error(AppError.TokenError.ExpiredToken)
                is Result.Success -> Result.Success(true)
            }
        }
    }
}