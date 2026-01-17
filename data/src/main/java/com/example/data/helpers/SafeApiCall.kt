package com.example.data.helpers

import android.util.Log
import com.example.common.errorhandler.AppError
import com.example.common.errorhandler.Result
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

private const val TAG = "SafeApiCall"

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Result<T, AppError>
): Result<T, AppError> {
    return try {
        apiCall()
    } catch (e: CancellationException) {
        throw e
    } catch (e: SocketTimeoutException) {
        Log.e(TAG, "Request timeout", e)
        Result.Error(AppError.Network.Timeout)
    } catch (_: SSLException) {
        Result.Error(AppError.Unknown("Security/SSL Configuration Error"))
    } catch (e: IOException) {
        Log.e(TAG, "Network error", e)
        Result.Error(AppError.Network.NoInternetConnection)
    } catch (e: HttpException) {
        Log.e(TAG, "HTTP error: ${e.code()}", e)
        val error = handleHttpException(e)
        Result.Error(error)
    } catch (e: UnknownHostException) {
        Log.e(TAG, "Unknown host - no internet connection", e)
        Result.Error(AppError.Network.NoInternetConnection)
    } catch (e: Exception) {
        Log.e(TAG, "Unexpected error", e)
        val message = e.localizedMessage ?: "Unknown error occurred"
        Result.Error(AppError.Unknown(message))
    }
}

private fun handleHttpException(exception: HttpException): AppError {
    // Try to parse error message from response body
    val serverMessage = try {
        exception.response()?.errorBody()?.string()?.let { body ->
            JSONObject(body).optString("message", null)
        }
    } catch (e: Exception) {
        Log.w(TAG, "Failed to parse error body", e)
        null
    }

    return when (exception.code()) {
        // Client Errors (4xx)
        400 -> AppError.Validation.InvalidInputs
        401 -> AppError.Authentication.InvalidCredentials
        403 -> AppError.Authorization.AccessDenied
        404 -> AppError.Authentication.UserNotFound
        409 -> AppError.Authentication.UserAlreadyExists
        422 -> AppError.Validation.InvalidInputs
        429 -> AppError.Network.TooManyRequests

        // Server Errors (5xx)
        500 -> AppError.Network.ServerError(serverMessage ?: "Internal server error")
        502 -> AppError.Network.ServerError(serverMessage ?: "Bad gateway")
        503 -> AppError.Network.ServerError(serverMessage ?: "Service unavailable")
        504 -> AppError.Network.Timeout

        // Other
        else -> {
            Log.w(TAG, "Unhandled HTTP code: ${exception.code()}")
            AppError.Network.UnexpectedResponse(exception.code())
        }
    }
}