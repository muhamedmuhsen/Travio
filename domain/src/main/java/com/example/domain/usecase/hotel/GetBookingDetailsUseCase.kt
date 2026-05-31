package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.booking.BookingDetails
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetBookingDetailsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(reference: String): Result<BookingDetails, DataError> {
        return repository.getBookingDetails(reference)
    }
}
