package com.example.domain.usecase.booking

import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.repository.booking.BookingRepository
import javax.inject.Inject

class ConfirmFlightOrderUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(request: BookingRequest): Result<BookingResult> {
        return repository.confirmFlightOrder(request)
    }
}
