package com.example.common.errorhandler

sealed interface AppError : Error {

    val message: String get() = "An unexpected error occurred."

    data class Unknown(override val message: String = "Unknown error occurred") : AppError

    sealed interface Network : AppError {
        data class BadRequest(val details: String? = null) : Network {
            override val message: String
                get() = details ?: "Bad request"
        }

        data object NoInternetConnection : Network {
            override val message: String
                get() = "No internet connection."
        }

        data object Timeout : Network {
            override val message: String
                get() = "The request timed out."
        }

        data class ServerError(val details: String? = null) : Network {
            override val message: String
                get() = details ?: "Server error occurred."
        }

        data class UnexpectedResponse(val code: Int) : Network {
            override val message: String
                get() = "Unexpected server response (code: $code)."
        }

        data object TooManyRequests : Network {
            override val message: String
                get() = "Too many requests. Please try again later."
        }
    }

    sealed interface Validation : AppError {
        data object InvalidInputs : Validation {
            override val message: String
                get() = "Please check your inputs."
        }

        data object MissingFields : Validation {
            override val message: String
                get() = "Please fill out all the fields."
        }

        data object InvalidEmailFormat : Validation {
            override val message: String
                get() = "Invalid email format."
        }

        data object WeakPassword : Validation {
            override val message: String
                get() = "Password is too weak."
        }

        data object PasswordMismatch : Validation {
            override val message: String
                get() = "Passwords don't match."
        }

        data object ShortName : Validation {
            override val message: String
                get() = "The name is too short."
        }
    }

    sealed interface Authentication : AppError {
        data object UserCancelled : Authentication {
            override val message: String
                get() = "Sign-in was cancelled."
        }
        data object InvalidCredentials : Authentication {
            override val message: String
                get() = "Invalid email or password."
        }

        data object UserNotFound : Authentication {
            override val message: String
                get() = "User not found. Please sign up."
        }

        data object UserAlreadyExists : Authentication {
            override val message: String
                get() = "This user already exists."
        }

        data object RegistrationFailed : Authentication {
            override val message: String
                get() = "Couldn't create an account."
        }

        data object SignInFailed : Authentication {
            override val message: String
                get() = "Couldn't sign in to your account."
        }
    }

    sealed interface Verification : AppError {
        data object InvalidCode : Verification {
            override val message: String
                get() = "Invalid verification code."
        }

        data object CodeExpired : Verification {
            override val message: String
                get() = "Verification code has expired."
        }

        data object TooManyAttempts : Verification {
            override val message: String
                get() = "Too many attempts. Please try again later."
        }

        data class VerificationFailed(val reason: String) : Verification {
            override val message: String
                get() = reason
        }
    }

    sealed interface Authorization : AppError {
        data object AccessDenied : Authorization {
            override val message: String
                get() = "You don't have permission to access this resource."
        }

        data object AccountDisabled : Authorization {
            override val message: String
                get() = "Your account has been disabled."
        }

        data object AccountLocked : Authorization {
            override val message: String
                get() = "Your account has been locked. Please contact support."
        }
    }

    sealed interface TokenError : AppError {
        data object TokenNotFound : TokenError {
            override val message: String
                get() = "Authentication token not found."
        }

        data object InvalidToken : TokenError {
            override val message: String
                get() = "Invalid authentication token."
        }

        data object ExpiredToken : TokenError {
            override val message: String
                get() = "Session expired. Please log in again."
        }

        data object CouldNotGetClaims : TokenError {
            override val message: String
                get() = "Failed to extract token information."
        }

        data object DecodingFailed : TokenError {
            override val message: String
                get() = "Couldn't decode the token."
        }
    }

    sealed interface Data : AppError {
        data object InvalidData : Data {
            override val message: String
                get() = "The data received is invalid or corrupted."
        }

        data object NotFound : Data {
            override val message: String
                get() = "The requested data was not found."
        }

        data object ParsingError : Data {
            override val message: String
                get() = "Failed to parse the data."
        }
    }
}