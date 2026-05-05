package com.example.data.repository.booking

import com.example.data.mapper.booking.toDomain
import com.example.data.mapper.booking.toDto
import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import com.example.network.api.FlightBookingApi
import com.example.network.dto.flights.booking.FlightOrderRequestDto
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
            api.createPaymentIntent(PaymentIntentRequestDto(offerId, passengers.map { it.toDto() })).toDomain()
        }
    }

    override suspend fun confirmFlightOrder(request: BookingRequest): Result<BookingResult> {
        return runCatching {
            val orderRequest = FlightOrderRequestDto(
                offerId = request.offerId,
                passengers = request.passengers.map { it.toDto() }
            )
            // Using paymentIntentId as the idempotency key per Decision 2 in research.md
            api.confirmFlightOrder(
                idempotencyKey = request.paymentIntentId,
                request = orderRequest
            ).toDomain()
        }
    }
}
