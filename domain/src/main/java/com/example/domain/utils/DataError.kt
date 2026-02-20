package com.example.domain.utils

sealed interface DataError : Error {
    enum class Network : DataError {
        BadRequest,
        NoInternetConnection,
        Timeout,
        ServerError,
        UnexpectedResponse,
        TooManyRequests,
    }

    enum class Validation : DataError {
        InvalidInputs,
        MissingFields,
        InvalidEmailFormat,
        WeakPassword,
        PasswordMismatch,
        ShortName,
        InvalidOTPFormat,
        ShortFirstName,
        ShortLastName,
        MustHaveAtLeastOneFieldToUpdate,
        InvalidUri,
        EMPTY_FIRSTNAME,
        EMPTY_LASTNAME,
        ShortUsername
    }

    enum class Authentication : DataError {
        UserCancelled,
        UnauthorizedAccess,
        UserNotFound,
        UserAlreadyExists,
        RegistrationFailed,
        SignInFailed,
        UsernameAlreadyExists
    }

    enum class Verification : DataError {
        InvalidCode,
        CodeExpired,
        TooManyAttempts,
        VerificationFailed,
    }

    enum class Authorization : DataError {
        AccessDenied,
        AccountDisabled,
        AccountLocked,
    }

    enum class TokenError : DataError {
        TokenNotFound,
        InvalidToken,
        ExpiredToken,
        CouldNotGetClaims,
        DecodingFailed,

    }

    enum class Data : DataError {
        InvalidData,
        NotFound,
        ParsingError,
        UnknownError,
    }

    enum class Local : DataError {
        DiskFull
    }
}