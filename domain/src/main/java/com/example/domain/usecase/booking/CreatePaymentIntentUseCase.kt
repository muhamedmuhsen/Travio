package com.example.domain.usecase.booking

import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import javax.inject.Inject

class CreatePaymentIntentUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        offerId: String,
        passengers: List<Passenger>
    ): Result<PaymentIntentInfo> {
        return repository.createPaymentIntent(offerId, passengers)
    }
}
