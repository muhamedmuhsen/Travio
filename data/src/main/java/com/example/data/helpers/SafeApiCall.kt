package com.example.data.helpers

import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(apiCall: suspend () -> Result<T, AppError>): Result<T, AppError> {
    return try {
        apiCall()
    } catch (_: SocketTimeoutException) {
        Result.Error(AppError.Network.Timeout)
    } catch (_: IOException) {
        Result.Error(AppError.Network.NoInternetConnection)
    } catch (e: HttpException) {
        val error = when (e.code()) {
            401 -> AppError.Authentication.InvalidCredentials
            404 -> AppError.Authentication.UserNotFound
            409 -> AppError.Authentication.UserAlreadyExists
            else -> AppError.Network.Server
        }
        Result.Error(error)
    } catch (e: Exception) {
        val message = e.localizedMessage ?: "Unknown error occurred"
        Result.Error(AppError.Unknown(message))
    }
}