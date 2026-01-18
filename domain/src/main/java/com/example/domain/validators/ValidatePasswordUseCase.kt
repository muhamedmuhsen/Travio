package com.example.domain.validators

import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    private val PASSWORD_REGEX =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$".toRegex()

    operator fun invoke(password: String): Boolean {
        if (password.isEmpty()) {
            return false
        }
        return password.matches(PASSWORD_REGEX)
    }
}
