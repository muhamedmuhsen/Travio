package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.booking.BookingItem
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class GetUserBookingsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(): Result<List<BookingItem>, DataError> {
        return repository.getUserBookings()
    }
}
