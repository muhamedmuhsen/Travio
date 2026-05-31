package com.example.domain.usecase.hotel

import com.example.domain.model.hotel.booking.CancellationResult
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import javax.inject.Inject

class CancelBookingUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(reference: String): Result<CancellationResult, DataError> {
        return repository.cancelBooking(reference)
    }
}
