package com.example.domain.usecase.trip

import com.example.domain.model.trip.TripSyncEvent
import com.example.domain.repository.trip.TripApiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTripSyncEventsUseCase @Inject constructor(
    private val repository: TripApiRepository
) {
    operator fun invoke(): Flow<TripSyncEvent> {
        return repository.observeSyncEvents()
    }
}
