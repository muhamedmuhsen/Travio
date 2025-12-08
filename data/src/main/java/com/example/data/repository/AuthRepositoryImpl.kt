package com.example.data.repository

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.data.helpers.safeApiCall
import com.example.data.local.datastore.DataStoreManager
import com.example.data.mapper.toDomain
import com.example.domain.model.User
import com.example.domain.repository.auth.TokenManager
import com.example.domain.repository.auth.AuthRepository
import com.example.network.api.AuthApi
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.social.Provider
import com.example.network.dto.auth.signup.SignupRequest
import com.example.network.dto.auth.social.SocialLoginRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
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
        email: String, password: String, username: String, firstname: String, lastname: String
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

    override suspend fun signInWithGoogle(idToken: String): Result<User, AppError> {
        try {
            val request = SocialLoginRequest(provider = Provider.GOOGLE, token = idToken)
            val response = api.socialLogin(request)
            //val googleUserDto = googleAuthDataSource.signIn(activity)
            /*TODO: send the token to the backend*/
            // val response = api.socialSignin(request)
            /*TODO: save token to data store*/
            return Result.Success(response.user.toDomain())
        } catch (e: Exception) {
            return Result.Error(AppError.Authentication.SignInFailed)
        }

    }

    override suspend fun signInWithFacebook(accessToken: String): Result<String, AppError> {
        return safeApiCall {
            val request = SocialLoginRequest(provider = Provider.FACEBOOK, token = accessToken)

            val response = api.socialLogin(request)

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.SignInFailed)
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

    override suspend fun forgetPassword(email: String): Result<Unit, AppError> {
        return safeApiCall {
            val response = api.forgetPassword(ForgetPasswordRequest(email))
            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Authentication.InvalidCredentials)
            }
            Result.Success(Unit)
        }
    }

    override suspend fun sendVerificationCode(code: String): Result<Unit, AppError> {
        if (code.isBlank()) {
            return Result.Error(AppError.Verification.InvalidCode)
        }
        return safeApiCall {
            val response = api.sendVerificationCode(VerificationCodeRequest(code))

            if (!response.status || !response.isVerified) {
                val error = when {
                    response.message?.contains("expired", ignoreCase = true) == true ->
                        AppError.Verification.CodeExpired

                    response.message?.contains("invalid", ignoreCase = true) == true ->
                        AppError.Verification.InvalidCode

                    response.message?.contains("attempts", ignoreCase = true) == true ->
                        AppError.Verification.TooManyAttempts

                    else ->
                        AppError.Verification.VerificationFailed(
                            response.message ?: "Verification failed"
                        )
                }
                return@safeApiCall Result.Error(error)
            }

            Result.Success(Unit)
        }
    }

    override suspend fun resetPassword(newPassword: String): Result<Unit, AppError> {
        return safeApiCall {
            val response = api.resetPassword(ResetPasswordRequest(newPassword))

            if (!response.status) {
                return@safeApiCall Result.Error(AppError.Network.BadRequest(response.message))
            }
            Result.Success(Unit)
        }
    }
}