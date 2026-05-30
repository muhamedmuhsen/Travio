package com.example.domain.repository.booking

import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo

interface BookingRepository {
    suspend fun createPaymentIntent(
        offerId: String,
        passengers: List<Passenger>
    ): Result<PaymentIntentInfo>
}
