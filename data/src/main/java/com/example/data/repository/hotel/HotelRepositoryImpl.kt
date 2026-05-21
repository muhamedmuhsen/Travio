package com.example.data.repository.hotel

import com.example.data.mapper.hotel.toCached
import com.example.data.mapper.hotel.toDomain
import com.example.data.utils.safeApiCall
import com.example.database.hotel.NearbyHotelDao
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.HotelApi
import com.example.network.dto.hotel.HotelSearchRequestDto
import com.example.network.dto.hotel.OccupancyDto
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApi,
    private val nearbyHotelDao: NearbyHotelDao
) : HotelRepository {

    override suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        radiusInKm: Int,
        maxHotels: Int,
        hotelCodes: List<Int>
    ): Result<List<NearbyHotel>, DataError> {
        // 1. Check Cache First
        val expirationTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours ago
        val cached = nearbyHotelDao.getCachedHotels(
            latitude = latitude,
            longitude = longitude,
            checkIn = checkIn,
            checkOut = checkOut,
            expirationTime = expirationTime
        )

        if (cached.isNotEmpty()) {
            return Result.Success(cached.map { it.toDomain() })
        }

        val request = HotelSearchRequestDto(
            checkIn = checkIn,
            checkOut = checkOut,
            occupancies = listOf(
                OccupancyDto(
                    adults = 2,
                    children = 0,
                    childrenAges = emptyList()
                )
            ),
            latitude = latitude,
            longitude = longitude,
            radiusInKm = radiusInKm,
            hotelCodes = hotelCodes,
            maxHotels = maxHotels
        )
        val result = safeApiCall {
            api.searchHotels(request)
        }

        return when (result) {
            is Result.Success -> {
                val response = result.data
                if (!response.success) {
                    Result.Error(DataError.Logical(response.message))
                } else {
                    val hotels = response.data?.hotels?.map { it.toDomain() } ?: emptyList()

                    // Save to Cache
                    if (hotels.isNotEmpty()) {
                        nearbyHotelDao.deleteOldCache(latitude, longitude)
                        val timestamp = System.currentTimeMillis()
                        val cacheEntities = hotels.map {
                            it.toCached(latitude, longitude, checkIn, checkOut, timestamp)
                        }
                        nearbyHotelDao.insertHotels(cacheEntities)
                    }

                    Result.Success(hotels)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getHotelDetails(
        hotelCode: Int,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int?,
        childrenAges: String?
    ): Result<HotelDetails, DataError> {
        val result = safeApiCall {
            api.getHotelDetails(
                hotelCode = hotelCode,
                checkIn = checkIn,
                checkOut = checkOut,
                adults = adults,
                children = children,
                childrenAges = childrenAges
            )
        }

        return when (result) {
            is Result.Success -> {
                val response = result.data
                val data = response.data
                if (!response.success) {
                    Result.Error(DataError.Logical(response.message))
                } else if (data == null) {
                    Result.Error(DataError.Data.NotFound)
                } else {
                    Result.Success(data.toDomain())
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }
}
