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
    // P1DT4H55M -> 1d 4h 55m
    val regex = "P(?:(\\d+)D)?T?(?:(\\d+)H)?(?:(\\d+)M)?".toRegex()
    val match = regex.find(this) ?: return this
    val days = match.groups[1]?.value
    val hours = match.groups[2]?.value
    val minutes = match.groups[3]?.value

    return buildString {
        if (days != null) append("${days}d ")
        if (hours != null) append("${hours}h ")
        if (minutes != null) append("${minutes}m")
    }.trim().ifBlank { this }
}

fun String.toCurrencySymbol(): String {
    return when (this.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "EGP" -> "EGP"
        "JPY" -> "¥"
        "SAR" -> "SAR"
        "AED" -> "AED"
        "KWD" -> "KWD"
        "QAR" -> "QAR"
        "OMR" -> "OMR"
        "BHD" -> "BHD"
        "JOD" -> "JOD"
        "MAD" -> "MAD"
        "TND" -> "TND"
        "DZD" -> "DZD"
        "TRY" -> "₺"
        else -> this
    }
}
