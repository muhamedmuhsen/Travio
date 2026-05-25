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
        latitude: Double? = null,
        longitude: Double? = null,
        radiusInKm: Int = 50,
        maxHotels: Int = 10
    ): Result<List<NearbyHotel>, DataError> {
        val lat: Double
        val lng: Double

        if (latitude != null && longitude != null) {
            lat = latitude
            lng = longitude
        } else {
            when (val locationResult = locationRepository.getLastKnownLocation()) {
                is Result.Error -> return Result.Error(locationResult.error)
                is Result.Success -> {
                    lat = locationResult.data.latitude
                    lng = locationResult.data.longitude
                }
            }
        }

        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return hotelRepository.searchNearbyHotels(
            latitude = lat,
            longitude = lng,
            checkIn = today.format(formatter),
            checkOut = tomorrow.format(formatter),
            radiusInKm = radiusInKm,
            maxHotels = maxHotels
        )
    }
}
