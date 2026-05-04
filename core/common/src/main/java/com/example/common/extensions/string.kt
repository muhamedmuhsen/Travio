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

fun Double.toFormattedPrice(): String {
    return String.format("%.2f", this)
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

fun String.toFormattedTime(): String {
    // 2026-06-02T00:46:00 -> 00:46
    return try {
        val parts = this.split("T")
        if (parts.size > 1) {
            val timePart = parts[1]
            val timeParts = timePart.split(":")
            if (timeParts.size >= 2) {
                "${timeParts[0]}:${timeParts[1]}"
            } else {
                this
            }
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }
}

fun String.toFormattedDate(): String {
    // 2026-06-02T00:46:00 -> Jun 02
    return try {
        val parts = this.split("T")
        val datePart = parts[0]
        val dateParts = datePart.split("-")
        if (dateParts.size == 3) {
            val month = dateParts[1].toInt()
            val day = dateParts[2]
            val monthName = when (month) {
                1 -> "Jan"
                2 -> "Feb"
                3 -> "Mar"
                4 -> "Apr"
                5 -> "May"
                6 -> "Jun"
                7 -> "Jul"
                8 -> "Aug"
                9 -> "Sep"
                10 -> "Oct"
                11 -> "Nov"
                12 -> "Dec"
                else -> ""
            }
            "$monthName $day"
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }
}

fun String.toFullDate(): String {
    // 2026-06-02T00:46:00 -> Saturday, May 30, 2026 (simplified for now)
    return try {
        val parts = this.split("T")
        val datePart = parts[0]
        val dateParts = datePart.split("-")
        if (dateParts.size == 3) {
            val year = dateParts[0]
            val month = dateParts[1].toInt()
            val day = dateParts[2]
            val monthName = when (month) {
                1 -> "January"
                2 -> "February"
                3 -> "March"
                4 -> "April"
                5 -> "May"
                6 -> "June"
                7 -> "July"
                8 -> "August"
                9 -> "September"
                10 -> "October"
                11 -> "November"
                12 -> "December"
                else -> ""
            }
            "$monthName $day, $year"
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }
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
