package com.example.common.extensions

fun String?.isNull(): Boolean {
    return this == null
}

fun String?.isNotNull(): Boolean {
    return this != null
}

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$".toRegex()
    return this.matches(emailRegex)
}

fun String.isValidPassword(): Boolean {
    val passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$".toRegex()
    return this.matches(passwordRegex)
}

fun String.isValidName(): Boolean {
    return this.length > 2
}

fun String.isValidOTP(): Boolean {
    return this.length == 6
}

fun String.toFlightDuration(): String {
    // PT4H51M -> 4h 51m
    val regex = "PT(?:(\\d+)H)?(?:(\\d+)M)?".toRegex()
    val match = regex.find(this) ?: return this
    val hours = match.groups[1]?.value
    val minutes = match.groups[2]?.value

    return buildString {
        if (hours != null) append("${hours}h ")
        if (minutes != null) append("${minutes}m")
    }.trim().ifBlank { this }
}
