package com.example.common.extensions

fun String?.isNull(): Boolean {
    return this == null
}

fun String?.isNotNull(): Boolean {
    return this != null
}

fun String.isValidEmail(): Boolean {
    val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$".toRegex()
    return this.matches(EMAIL_REGEX)
}

fun String.isValidPassword(): Boolean {
    val PASSWORD_REGEX =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$".toRegex()
    return this.matches(PASSWORD_REGEX)
}

fun String.isValidName(): Boolean {
    return this.length > 2
}

fun String.isValidOTP(): Boolean {
    return this.length == 6
}