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
import com.example.network.dto.auth.Provider
import com.example.network.dto.auth.SignupRequest
import com.example.network.dto.auth.SocialLoginRequest

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

            dataStoreManager.saveToken(response.user.accessToken, "")
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
                return@safeApiCall Result.Error(AppError.Authentication.RegistrationFailed)
            }
            dataStoreManager.saveToken(response.user.accessToken, " ")
            dataStoreManager.setLoggedIn(true)

            val user = response.user.toDomain(
                //email, phone
            )
            Result.Success(user)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<String, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.GOOGLE, token = idToken)

            val response = api.socialSignin(request)

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.SigninFaild)
            }

            tokenManager.saveToken(accessToken = response.user.accessToken, refreshToken = "/////")
            //val user = response.user.toDomain()

            Result.Success(response.user.accessToken)
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<String, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.FACEBOOK, token = accessToken)

            val response = api.socialSignin(request)

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.SigninFaild)
            }

            tokenManager.saveToken(accessToken = response.user.accessToken, refreshToken = "/////")
            // val user = response.user.toDomain()

            Result.Success(response.user.accessToken)
        }
    }

    override suspend fun logout(): Result<Unit, AppError> {
        return safeApiCall {
            api.logout()

            dataStoreManager.clearTokens()
            dataStoreManager.setLoggedIn(false)
            dataStoreManager.clearCredentials()

            Result.Success(Unit)
        }
    }

    override suspend fun isAuthenticated(): Result<Boolean, AppError> {
//        return safeApiCall {
//            val token = tokenManager.getToken()
//
//            if (token.isNullOrEmpty()) {
//                return@safeApiCall Result.Success(false)
//            }
//
//            return@safeApiCall when (val result = tokenManager.isTokenExpired()) {
//                is Result.Error -> Result.Error(AppError.TokenError.ExpiredToken)
//                is Result.Success -> {
//                    val isExpired = result.data
//                    if (isExpired) {
//                        val refreshResult = tokenManager.getRefreshToken()
//                        return@safeApiCall when (refreshResult) {
//                            is Result.Success -> Result.Success(true)
//                            is Result.Error -> Result.Error(AppError.TokenError.InvalidToken)
//
//                        }
//                    }
//                    Result.Success(true)
//                }
//            }
//        }
        return Result.Success(true)
    }

    override suspend fun refreshToken(): Result<Unit, AppError> {
        return safeApiCall {
            val refreshToken = tokenManager.getRefreshToken() ?: return@safeApiCall Result.Error(
                AppError.TokenError.TokenNotFound
            )

            val response = api.refreshToken(refreshToken).execute()

            if (!response.isSuccessful || response.body() == null) {
                tokenManager.clearTokens()
                dataStoreManager.setLoggedIn(false)
                dataStoreManager.clearCredentials()
            }

            val refreshResponse = response.body()!!

            val newRefreshToken = refreshResponse.user.refreshToken
            dataStoreManager.saveToken(refreshResponse.user.accessToken, newRefreshToken)
            Result.Success(Unit)

        }
    }
}