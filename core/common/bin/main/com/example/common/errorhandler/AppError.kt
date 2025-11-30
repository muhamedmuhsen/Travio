package com.example.common.errorhandler

sealed interface AppError : Error {

    val message: String get() = "An unexpected error occurred."

    data class Unknown(override val message: String = "Unknown error occurred") : AppError

    sealed interface Network : AppError {
        data object NoInternetConnection : Network {
            override val message: String
                get() = "No internet connection."
        }

        data object Timeout : Network {
            override val message: String
                get() = "The request timed out."
        }

        data object Server : Network {
            override val message: String
                get() = "Server error occurred."
        }
    }

    sealed interface Validation : AppError {
        data object InvalidInputs : Validation {
            override val message: String
                get() = "Please check your inputs."
        }

        data object MissingFields : Validation {
            override val message: String
                get() = "Please fill out all the fields"
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
                get() = "Passwords don’t match."
        }

        data object ShortName : Validation {
            override val message: String
                get() = "The name is too short"
        }
    }

    sealed interface Authentication : AppError {
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
                get() = "Couldn't create an account"
        }

        data object SigninFaild : Authentication {
            override val message: String
                get() = "Couldn't login an account"
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

        data object CouldNotGetClaims : TokenError {  // Fixed typo
            override val message: String
                get() = "Failed to extract token information."
        }

        data object DecodedException : TokenError {
            override val message: String
                get() = "Couldn't decode the token"
        }
    }

    sealed interface Data : AppError {
        data object NoData : Data {
            override val message: String
                get() = "No Data found"
        }
    }
}