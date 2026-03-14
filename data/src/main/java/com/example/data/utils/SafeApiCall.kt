package com.example.data.utils

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T, DataError> {
    return try {
        val result = apiCall()
        Timber.d("safeApiCall: request succeeded")
        Result.Success(result)
    } catch (e: HttpException) {
        val error = mapHttpError(e)
        Timber.e(
            e,
            "safeApiCall: HTTP error -> code=${e.code()}, message=${e.message()}, mappedError=$error"
        )
        Result.Error(error)
    } catch (e: UnknownHostException) {
        Timber.e(e, "safeApiCall: no internet connection -> host unreachable (${e.message})")
        Result.Error(DataError.Network.NoInternetConnection)
    } catch (e: SocketTimeoutException) {
        Timber.e(e, "safeApiCall: request timed out (${e.message})")
        Result.Error(DataError.Network.Timeout)
    } catch (e: FileNotFoundException) {
        Timber.e(e, "safeApiCall: content not found -> ${e.message}")
        Result.Error(DataError.Validation.InvalidUri)
    } catch (e: SecurityException) {
        Timber.e(e, "safeApiCall: security error accessing content -> ${e.message}")
        Result.Error(DataError.Validation.InvalidUri)
    } catch (e: IOException) {
        Timber.e(e, "safeApiCall: I/O error -> no internet connection (${e.message})")
        Result.Error(DataError.Network.NoInternetConnection)
    } catch (e: CancellationException) {
        Timber.w("safeApiCall: coroutine was cancelled -> rethrowing")
        throw e
    } catch (e: Exception) {
        Timber.e(e, "safeApiCall: unexpected error -> ${e::class.simpleName}: ${e.message}")
        Result.Error(DataError.Network.UnexpectedResponse)
    }
}

private fun mapHttpError(e: HttpException): DataError {
    val error = when (e.code()) {
        400 -> DataError.Network.BadRequest
        401 -> {
            val errorBody = e.response()?.errorBody()?.string().orEmpty()
            Timber.w("mapHttpError: 401 Unauthorized -> errorBody=$errorBody")
            if (errorBody.contains("Username is already registered", ignoreCase = true)) {
                DataError.Authentication.UsernameAlreadyExists
            } else {
                DataError.Authentication.UnauthorizedAccess
            }
        }
        404 -> DataError.Authentication.UserNotFound
        408 -> DataError.Network.Timeout
        429 -> DataError.Network.TooManyRequests
        in 500..599 -> DataError.Network.ServerError
        else -> DataError.Network.UnexpectedResponse
    }
    Timber.w("mapHttpError: HTTP ${e.code()} mapped to -> $error")
    return error
}
