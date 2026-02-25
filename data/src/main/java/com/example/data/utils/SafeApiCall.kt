package com.example.data.utils

import android.util.Log
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T, DataError> {
    val TAG = "DestinationsTAG"

    return try {
        val result = apiCall()
        Result.Success(result)
    } catch (e: HttpException) {
        Log.d(TAG, "Couldn't Get All Destinations", e)
        val error = mapHttpError(e)
        Result.Error(error)
    } catch (e: UnknownHostException) {
        Log.d(TAG, "Couldn't Get All Destinations", e)

        Result.Error(DataError.Network.NoInternetConnection)
    } catch (e: SocketTimeoutException) {
        Log.d(TAG, "Couldn't Get All Destinations", e)

        Result.Error(DataError.Network.Timeout)
    } catch (e: IOException) {
        Log.d(TAG, "Couldn't Get All Destinations", e)

        Result.Error(DataError.Network.NoInternetConnection)
    } catch (e: CancellationException) {
        Log.d(TAG, "Couldn't Get All Destinations", e)

        throw e
    } catch (e: Exception) {
        Log.d(TAG, "Couldn't Get All Destinations", e)

        Result.Error(DataError.Network.UnexpectedResponse)
    }
}

private fun mapHttpError(e: HttpException): DataError {
    val error = when (e.code()) {
        400 -> DataError.Network.BadRequest
        401 -> {
            val errorBody = e.response()?.errorBody()?.string().orEmpty()
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
    return error
}
