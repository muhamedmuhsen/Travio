package com.example.domain.model.hotel.booking

enum class BookingStatus(val value: String) {
    CONFIRMED("Confirmed"),
    PENDING("Pending"),
    CANCELLED("Cancelled"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(status: String?): BookingStatus {
            return entries.find { it.value.equals(status, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
