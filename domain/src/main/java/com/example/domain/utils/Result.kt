package com.example.domain.utils

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

val <D, E : RootError> Result<D, E>.isSuccess: Boolean
    get() = this is Result.Success

val <D, E : RootError> Result<D, E>.isError: Boolean
    get() = this is Result.Error
