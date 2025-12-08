package com.example.domain.validators

import javax.inject.Inject


class ValidateEmailUseCase @Inject constructor() {
    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$".toRegex()
    operator fun invoke(email: String): Boolean {
        return email.matches(EMAIL_REGEX)
    }
}