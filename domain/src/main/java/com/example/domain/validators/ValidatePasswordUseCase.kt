package com.example.domain.validators

class ValidatePasswordUseCase {
    private val PASSWORD_REGEX =
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{9,}$".toRegex()

    operator fun invoke(password: String): Boolean {
        if (password.isEmpty()) {
            return false
        }
        return password.matches(PASSWORD_REGEX)

    }
}