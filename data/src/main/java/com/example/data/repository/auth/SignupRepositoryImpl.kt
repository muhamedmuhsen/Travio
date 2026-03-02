package com.example.data.repository.auth

import com.example.data.utils.safeApiCall
import com.example.domain.repository.auth.SignupRepository
import com.example.domain.repository.auth.TokenProvider
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.AuthApi
import com.example.network.dto.auth.signup.SignupRequest
import javax.inject.Inject

class SignupRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenProvider: TokenProvider
) : SignupRepository {
    override suspend fun signup(
        email: String,
        password: String,
        username: String,
        firstname: String,
        lastname: String,
        confirmPassword: String
    ): Result<Unit, DataError> =
        safeApiCall {
            val response = api.signup(
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
                accessToken = response.token,
                refreshToken = response.refreshToken,
                refreshTokenExpiryEpochMs = response.refreshTokenExpiration.toLongOrNull()
            )
        }
}
