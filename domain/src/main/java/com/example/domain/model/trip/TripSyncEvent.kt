package com.example.domain.model.trip

sealed interface TripSyncEvent {
    data class FavoriteToggled(val tripId: Int, val isFavorite: Boolean) : TripSyncEvent
    data class TripDeleted(val tripId: Int) : TripSyncEvent
}
