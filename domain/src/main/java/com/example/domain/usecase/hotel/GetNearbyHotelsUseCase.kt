package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetNearbyHotelsUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        radiusInKm: Int = 50,
        maxHotels: Int = 10
    ): Result<List<NearbyHotel>, DataError> {
        return when (val locationResult = locationRepository.getLastKnownLocation()) {
            is Result.Error -> {
                Result.Error(locationResult.error)
            }
            is Result.Success -> {
                val location = locationResult.data
                val today = LocalDate.now()
                val tomorrow = today.plusDays(1)
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                hotelRepository.searchNearbyHotels(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    checkIn = today.format(formatter),
                    checkOut = tomorrow.format(formatter),
                    radiusInKm = radiusInKm,
                    maxHotels = maxHotels
                )
            }
        }
    }
}
