package com.example.data.local.datastore

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import com.example.domain.model.DecodedToken
import com.example.domain.repository.Auth.TokenManager
import java.util.Date
import javax.inject.Inject

class TokenManagerImpl @Inject constructor(private val dataStoreManager: DataStoreManager) :
    TokenManager {
    override suspend fun decodeToken(): Result<DecodedToken, AppError> {
        return try {
            val token =
                dataStoreManager.getToken()
                    ?: return Result.Error(AppError.TokenError.TokenNotFound)
            val jwt = JWT(token)
            Result.Success(
                DecodedToken(
                    userId = (jwt.subject ?: jwt.getClaim("userId").asString()),
                    expiresAt = jwt.expiresAt,
                    claims = mapOf(
                        "userId" to jwt.subject, "expiresAt" to jwt.expiresAt
                    )
                )
            )
        } catch (_: DecodeException) {
            Result.Error(AppError.TokenError.DecodedException)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: "Unknown error occurred"
            Result.Error(AppError.Unknown(message))
        }
    }

    override suspend fun isTokenExpired(): Result<Boolean, AppError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> {
                val expiresAt =
                    result.data.expiresAt ?: return Result.Error(AppError.TokenError.InvalidToken)
                Result.Success(expiresAt.before(Date()))
            }
        }
    }


    override suspend fun getTokenClaims(): Result<Map<String, Any?>, AppError> {
        return when (val result = decodeToken()) {
            is Result.Error -> Result.Error(AppError.TokenError.CouldNotGetClaims)
            is Result.Success -> Result.Success(result.data.claims)
        }
    }


}