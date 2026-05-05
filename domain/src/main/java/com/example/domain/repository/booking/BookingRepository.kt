package com.example.domain.repository.booking

import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.PaymentIntentInfo

interface BookingRepository {
    suspend fun createPaymentIntent(offerId: String): Result<PaymentIntentInfo>
    suspend fun confirmFlightOrder(request: BookingRequest): Result<BookingResult>
}
