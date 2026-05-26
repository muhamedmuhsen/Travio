package com.example.data.repository.trip

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface TripFavoriteSyncEvent {
    data class FavoriteToggled(val tripId: Int, val isFavorite: Boolean) : TripFavoriteSyncEvent
    data class TripDeleted(val tripId: Int) : TripFavoriteSyncEvent
}

@Singleton
class TripFavoriteSyncStore @Inject constructor() {
    private val _syncEvents = MutableSharedFlow<TripFavoriteSyncEvent>(extraBufferCapacity = 10)
    val syncEvents = _syncEvents.asSharedFlow()

    fun emitFavoriteToggled(
        tripId: Int,
        isFavorite: Boolean
    ) {
        _syncEvents.tryEmit(TripFavoriteSyncEvent.FavoriteToggled(tripId, isFavorite))
    }

    fun emitTripDeleted(tripId: Int) {
        _syncEvents.tryEmit(TripFavoriteSyncEvent.TripDeleted(tripId))
    }
}
