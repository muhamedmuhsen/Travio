package com.example.domain.model.destination

sealed class LocationOutcome {
    data class Available(val location: UserLocation) : LocationOutcome()
    data class PermissionDenied(val isPermanentlyDenied: Boolean) : LocationOutcome()
    data class Unavailable(val reason: UnavailableReason) : LocationOutcome()
    data class Stale(val ageMillis: Long, val location: UserLocation) : LocationOutcome()

    enum class UnavailableReason {
        PROVIDER_DISABLED,
        TIMEOUT,
        NULL_RESULT,
        UNKNOWN_ERROR
    }
}
