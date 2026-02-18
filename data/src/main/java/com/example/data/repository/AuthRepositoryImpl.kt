package com.example.data.repository

import com.example.domain.repository.auth.TokenProvider
import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.repository.prefernces.CredentialsManager
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.GoogleLoginRequest
import com.example.network.dto.auth.SendVerifyOTPRequest
import com.example.network.dto.auth.VerifyEmailRequest
import com.example.network.dto.auth.forgetpassword.ForgetPasswordRequest
import com.example.network.dto.auth.forgetpassword.ResetPasswordRequest
import com.example.network.dto.auth.forgetpassword.VerificationCodeRequest
import com.example.network.dto.auth.login.LoginRequest
import com.example.network.dto.auth.logout.LogoutRequest
import com.example.network.dto.auth.signup.SignupRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val credentialsManager: CredentialsManager,
    private val preferencesManager: PreferencesManager,
    private val tokenProvider: TokenProvider,
) : AuthRepository {

    override suspend fun login(
        email: String, password: String, isRememberMeChecked: Boolean
    ): Result<Unit, DataError> = safeApiCall {
        val response = api.login(LoginRequest(email, password))
        tokenProvider.saveTokens(
            response.token,
            response.refreshToken
        )
        preferencesManager.setLoggedIn(true)
        if (isRememberMeChecked) {
            credentialsManager.saveCredentials(
                email, password, true
            )
        }
    }

    override suspend fun signup(
        email: String, password: String, username: String, firstname: String, lastname: String,
        confirmPassword: String

    ): Result<Unit, DataError> = safeApiCall {
        val response =
            api.signup(
                SignupRequest(
                    firstname = firstname,
                    lastname = lastname,
                    email = email,
                    username = username,
                    password = password,
                    confirmPassword = confirmPassword
                )
            )

        tokenProvider.saveTokens(
            response.token,
            response.refreshToken
        )
        //preferencesManager.setLoggedIn(true)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit, DataError> = safeApiCall {
        val request = GoogleLoginRequest(idToken)
        val response = api.googleLogin(request)

        tokenProvider.saveTokens(
            response.token,
            response.refreshToken
        )
        preferencesManager.setLoggedIn(true)
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<Unit, DataError> =
        safeApiCall {
            // Implementation placeholder
        }

    override suspend fun logout(): Result<Unit, DataError> {
        val refreshToken = tokenProvider.getRefreshToken()
        val result = safeApiCall {
            api.logout(LogoutRequest(refreshToken))
        }
        tokenProvider.clearTokens()
        preferencesManager.setLoggedIn(false)
        credentialsManager.clearCredentials()
        return result
    }

    override suspend fun verifyEmail(): Result<Unit, DataError> {
        val request = VerifyEmailRequest(email = "")
        val response = safeApiCall { api.verifyEmail(request) }
        return response
    }

    override suspend fun isAuthenticated(): Result<Boolean, DataError> {
        return Result.Success(preferencesManager.isLoggedIn())
    }

    override suspend fun refreshToken(): Result<Unit, DataError> {
        return Result.Success(Unit)
    }

    override suspend fun forgetPassword(email: String): Result<Unit, DataError> = safeApiCall {
        api.forgetPassword(ForgetPasswordRequest(email))
    }

    override suspend fun sendVerificationCode(
        email: String,
        code: String
    ): Result<Unit, DataError> = safeApiCall {
        val response =
            api.sendVerificationCode(VerificationCodeRequest(email = email, otp = code))
        tokenProvider.saveResetToken(response.resetToken)
    }

    override suspend fun sendVerifyEmailOtp(email: String): Result<String, DataError> =
        safeApiCall {
            val request = SendVerifyOTPRequest(email)
            val response = api.sendVerifyEmailOtp(request)
            return@safeApiCall response.expiresOn
        }

    override suspend fun resetPassword(
        resetToken: String,
        email: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit, DataError> = safeApiCall {
        api.resetPassword(
            ResetPasswordRequest(
                resetToken,
                email,
                newPassword,
                confirmNewPassword
            )
        )
        handlePostResetPassword()
    }

    override suspend fun verifyEmail(
        email: String,
        otp: String
    ): Result<Unit, DataError> = safeApiCall {
        val request = VerifyEmailRequest(email, otp)
        val response = api.verifyEmail(request)

        //tokenProvider.saveTokens(response.token, response.refreshToken)

        preferencesManager.setLoggedIn(true)
    }

    private suspend fun handlePostResetPassword() {
        tokenProvider.clearResetToken()
    }
}
