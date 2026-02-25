package com.example.domain.usecase.destinations

import com.example.domain.model.destination.Destination
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.utils.DataError
import javax.inject.Inject
import com.example.domain.utils.Result

class GetNearbyDestinationsUseCase @Inject constructor
    (
    private val locationRepository: LocationRepository,
    private val destinationsRepository: DestinationsRepository
) {
    suspend operator fun invoke(): Result<List<Destination>, DataError> {
        return when (val locationResult = locationRepository.getLastKnownLocation()) {
            is Result.Error -> {
                Result.Error(DataError.Location.CouldNotGetTheLocation)
            }

            is Result.Success -> {
                val location = locationResult.data

                destinationsRepository.getNearbyDestinations(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
    }

}