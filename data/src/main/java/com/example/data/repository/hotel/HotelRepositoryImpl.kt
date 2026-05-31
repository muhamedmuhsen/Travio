package com.example.data.repository.hotel

import com.example.data.mapper.hotel.booking.toDomain
import com.example.data.mapper.hotel.toCached
import com.example.data.mapper.hotel.toDomain
import com.example.data.mapper.hotel.toDto
import com.example.data.utils.safeApiCall
import com.example.database.hotel.HotelDetailsDao
import com.example.database.hotel.NearbyHotelDao
import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.booking.BookingDetails
import com.example.domain.model.hotel.booking.BookingItem
import com.example.domain.model.hotel.booking.CancellationResult
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.HotelApi
import com.example.network.dto.hotel.HotelSearchRequestDto
import com.example.network.dto.hotel.OccupancyDto
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApi,
    private val nearbyHotelDao: NearbyHotelDao,
    private val hotelDetailsDao: HotelDetailsDao
) : HotelRepository {

    override suspend fun checkoutHotel(request: HotelCheckoutRequest): Result<HotelCheckoutResult, DataError> {
        val requestDto = request.toDto()
        val result = safeApiCall {
            api.checkoutHotel(requestDto)
        }
        return when (result) {
            is Result.Success -> {
                val response = result.data
                val dataDto = response.data
                if (!response.success) {
                    Result.Error(DataError.Logical(response.message))
                } else if (dataDto == null) {
                    Result.Error(DataError.Data.NotFound)
                } else {
                    Result.Success(dataDto.toDomain())
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

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
        // 1. Check Cache First
        val childrenAgesStr = childrenAges ?: ""
        val childrenCount = children ?: 0
        val expirationTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours ago

        val cached = hotelDetailsDao.getHotelDetails(
            code = hotelCode,
            checkIn = checkIn,
            checkOut = checkOut,
            adults = adults,
            children = childrenCount,
            childrenAges = childrenAgesStr,
            expirationTime = expirationTime
        )

        if (cached != null) {
            return Result.Success(cached.toDomain())
        }

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
                    val domainHotel = data.toDomain()

                    // Save to Cache
                    hotelDetailsDao.deleteOldCache(
                        code = hotelCode,
                        checkIn = checkIn,
                        checkOut = checkOut,
                        adults = adults,
                        children = childrenCount,
                        childrenAges = childrenAgesStr
                    )

                    val timestamp = System.currentTimeMillis()
                    val cacheEntity = domainHotel.toCached(
                        checkIn = checkIn,
                        checkOut = checkOut,
                        adults = adults,
                        children = childrenCount,
                        childrenAges = childrenAgesStr,
                        timestamp = timestamp
                    )
                    hotelDetailsDao.insertHotelDetails(cacheEntity)

                    Result.Success(domainHotel)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun searchHotels(
        destination: String,
        checkIn: String,
        checkOut: String,
        occupancies: List<com.example.domain.model.hotel.Occupancy>
    ): Result<List<NearbyHotel>, DataError> {
        val request = HotelSearchRequestDto(
            checkIn = checkIn,
            checkOut = checkOut,
            occupancies = occupancies.map { it.toDto() },
            destinationName = destination,
            hotelCodes = listOf(0)
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
                    Result.Success(hotels)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    private fun com.example.domain.model.hotel.Occupancy.toDto(): OccupancyDto {
        return OccupancyDto(
            adults = this.adults,
            children = this.children,
            childrenAges = this.childrenAges
        )
    }

    override suspend fun getUserBookings(): Result<List<BookingItem>, DataError> {
        val result = safeApiCall {
            api.getUserBookings()
        }

        return when (result) {
            is Result.Success -> {
                val response = result.data
                if (!response.success) {
                    Result.Error(DataError.Logical(response.message))
                } else {
                    val bookings = response.data?.map { it.toDomain() } ?: emptyList()
                    Result.Success(bookings)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getBookingDetails(reference: String): Result<BookingDetails, DataError> {
        val result = safeApiCall {
            api.getBookingDetails(reference)
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

    override suspend fun cancelBooking(reference: String): Result<CancellationResult, DataError> {
        val result = safeApiCall {
            api.cancelBooking(reference)
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
