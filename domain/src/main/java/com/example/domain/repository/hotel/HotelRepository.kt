package com.example.domain.repository.hotel

import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.booking.BookingDetails
import com.example.domain.model.hotel.booking.BookingItem
import com.example.domain.model.hotel.booking.CancellationResult
import com.example.domain.utils.DataError
import com.example.domain.utils.Result

interface HotelRepository {
    suspend fun searchNearbyHotels(
        latitude: Double,
        longitude: Double,
        checkIn: String,
        checkOut: String,
        radiusInKm: Int = 50,
        maxHotels: Int = 10,
        hotelCodes: List<Int> = listOf(1)
    ): Result<List<NearbyHotel>, DataError>

    suspend fun searchHotels(
        destination: String,
        checkIn: String,
        checkOut: String,
        occupancies: List<com.example.domain.model.hotel.Occupancy>
    ): Result<List<NearbyHotel>, DataError>

    suspend fun getHotelDetails(
        hotelCode: Int,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int? = null,
        childrenAges: String? = null
    ): Result<com.example.domain.model.hotel.HotelDetails, DataError>

    suspend fun checkoutHotel(request: HotelCheckoutRequest): Result<HotelCheckoutResult, DataError>

    suspend fun getUserBookings(): Result<List<BookingItem>, DataError>

    suspend fun getBookingDetails(reference: String): Result<BookingDetails, DataError>

    suspend fun cancelBooking(reference: String): Result<CancellationResult, DataError>
}
