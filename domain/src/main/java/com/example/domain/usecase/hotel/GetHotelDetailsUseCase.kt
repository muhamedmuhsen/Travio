package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.HotelDetails
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetHotelDetailsUseCase @Inject constructor(
    private val hotelRepository: HotelRepository
) {
    suspend operator fun invoke(
        hotelCode: Int,
        checkIn: String? = null,
        checkOut: String? = null,
        adults: Int = 2,
        children: Int? = null,
        childrenAges: String? = null
    ): Result<HotelDetails, DataError> {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val finalCheckIn = checkIn ?: today.format(formatter)
        val finalCheckOut = checkOut ?: tomorrow.format(formatter)

        return hotelRepository.getHotelDetails(
            hotelCode = hotelCode,
            checkIn = finalCheckIn,
            checkOut = finalCheckOut,
            adults = adults,
            children = children,
            childrenAges = childrenAges
        )
    }
}
