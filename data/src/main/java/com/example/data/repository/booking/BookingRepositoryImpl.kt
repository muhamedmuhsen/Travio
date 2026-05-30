package com.example.data.repository.booking

import com.example.data.mapper.booking.toDomain
import com.example.data.mapper.booking.toDto
import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import com.example.network.api.FlightBookingApi
import com.example.network.dto.flights.booking.PaymentIntentRequestDto
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: FlightBookingApi
) : BookingRepository {

    override suspend fun createPaymentIntent(
        offerId: String,
        passengers: List<Passenger>
    ): Result<PaymentIntentInfo> {
        return runCatching {
            val response = api.createPaymentIntent(PaymentIntentRequestDto(offerId, passengers.map { it.toDto() }))
            val payload = response.data ?: throw IllegalStateException(response.message ?: "Payment intent response missing data")
            if (!response.success) {
                throw IllegalStateException(response.errors?.joinToString() ?: response.message ?: "Payment intent creation failed")
            }
            payload.toDomain()
        }
    }
}
