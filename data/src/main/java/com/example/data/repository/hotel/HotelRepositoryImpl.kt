package com.example.data.repository.hotel

import com.example.data.mapper.hotel.toDomain
import com.example.data.utils.safeApiCall
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.HotelApi
import com.example.network.dto.hotel.HotelSearchRequestDto
import com.example.network.dto.hotel.OccupancyDto
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApi
) : HotelRepository {

    override suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        radiusInKm: Int,
        maxHotels: Int
    ): Result<List<NearbyHotel>, DataError> {
        val request = HotelSearchRequestDto(
            checkIn = checkIn,
            checkOut = checkOut,
            occupancies = listOf(
                OccupancyDto(
                    rooms = 1,
                    adults = 2,
                    children = 0
                )
            ),
            latitude = latitude,
            longitude = longitude,
            radiusInKm = radiusInKm,
            maxHotels = maxHotels
        )
        return safeApiCall {
            val response = api.searchHotels(request)
            response.map { it.toDomain() }
        }
    }
}
