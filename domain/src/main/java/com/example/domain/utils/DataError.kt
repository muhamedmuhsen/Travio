package com.example.domain.utils

sealed interface DataError : Error {

    object UnknownError : DataError
    enum class Network : DataError {
        BadRequest,
        NoInternetConnection,
        Timeout,
        ServerError,
        UnexpectedResponse,
        TooManyRequests
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
        EmptyFirstName,
        EmptyLastName,
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
        VerificationFailed
    }

    enum class Authorization : DataError {
        AccessDenied,
        AccountDisabled,
        AccountLocked
    }

    enum class TokenError : DataError {
        TokenNotFound,
        InvalidToken,
        ExpiredToken,
        CouldNotGetClaims,
        DecodingFailed
    }

    enum class Data : DataError {
        InvalidData,
        NotFound,
        ParsingError,

    }

    enum class Local : DataError {
        DiskFull,
        InvalidInput,
        ConstraintViolation,
        RecordNotFound,
        DatabaseError,
        UnknownError
    }

    enum class Location : DataError {
        CouldNotGetTheLocation,
        PermissionDenied,
        LocationDisabled,
        Timeout
    }
}
