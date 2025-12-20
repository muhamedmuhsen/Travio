package com.example.common.errorhandler



fun AppError.toUserMessage(): String = when (this) {
    // Network errors
    is AppError.Network.NoInternetConnection ->
        "No internet connection. Please check your network."

    is AppError.Network.Timeout ->
        "Request timed out. Please try again."

    is AppError.Network.ServerError ->
        "Server error. Please try again later."

    is AppError.Network.TooManyRequests ->
        "Too many attempts. Please wait a moment."

    is AppError.Network.BadRequest ->
        details ?: "Invalid request."

    is AppError.Network.UnexpectedResponse ->
        "Unexpected error (code: $code)."

    // Authentication errors
    AppError.Authentication.InvalidCredentials ->
        "Invalid email or password."

    AppError.Authentication.UserNotFound ->
        "Account not found."

    AppError.Authentication.UserAlreadyExists ->
        "An account with this email already exists."

    AppError.Authentication.SignInFailed ->
        "Sign in failed. Please try again."

    AppError.Authentication.RegistrationFailed ->
        "Registration failed. Please try again."

    // Validation errors
    AppError.Validation.InvalidEmailFormat ->
        "Please enter a valid email address."

    AppError.Validation.WeakPassword ->
        "Password must be at least 8 characters."

    AppError.Validation.MissingFields ->
        "Please fill in all required fields."

    AppError.Validation.PasswordMismatch ->
        "Passwords do not match."

    AppError.Validation.InvalidInputs ->
        "Please check your input."

    AppError.Validation.ShortName ->
        "Name is too short."

    // Verification errors
    AppError.Verification.InvalidCode ->
        "Invalid verification code."

    AppError.Verification.CodeExpired ->
        "Verification code expired. Request a new one."

    AppError.Verification.TooManyAttempts ->
        "Too many attempts. Please try again later."

    is AppError.Verification.VerificationFailed ->
        reason

    // Authorization errors
    AppError.Authorization.AccessDenied ->
        "Access denied."

    AppError.Authorization.AccountDisabled ->
        "Your account has been disabled."

    AppError.Authorization.AccountLocked ->
        "Your account is locked."

    // Token errors
    AppError.TokenError.ExpiredToken,
    AppError.TokenError.InvalidToken ->
        "Session expired. Please sign in again."

    AppError.TokenError.TokenNotFound,
    AppError.TokenError.CouldNotGetClaims,
    AppError.TokenError.DecodingFailed ->
        "Authentication error. Please sign in again."

    // Data errors
    AppError.Data.NotFound ->
        "Data not found."

    AppError.Data.InvalidData,
    AppError.Data.ParsingError ->
        "Data error. Please try again."

    // Unknown
    is AppError.Unknown ->
        message.ifBlank { "An unexpected error occurred." }

    AppError.Authentication.UserCancelled -> "Use Cancelled the login operation"
}