package com.example.domain.usecase.booking

import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfirmFlightOrderUseCaseTest {

    private val fakeRepository = object : BookingRepository {
        override suspend fun createPaymentIntent(offerId: String, passengers: List<Passenger>): Result<PaymentIntentInfo> {
            TODO("Not needed")
        }

        override suspend fun confirmFlightOrder(request: BookingRequest): Result<BookingResult> {
            return if (request.offerId == "valid") {
                Result.success(BookingResult("ord_123", "ABCDEF", "confirmed"))
            } else {
                Result.failure(Exception("Error"))
            }
        }
    }

    private val useCase = ConfirmFlightOrderUseCase(fakeRepository)

    @Test
    fun `given valid request then returns success`() = runTest {
        val request = BookingRequest("valid", emptyList(), "pi_123")
        val result = useCase(request)
        assertTrue(result.isSuccess)
        assertEquals("ABCDEF", result.getOrNull()?.pnr)
    }

    @Test
    fun `given invalid request then returns failure`() = runTest {
        val request = BookingRequest("invalid", emptyList(), "pi_123")
        val result = useCase(request)
        assertTrue(result.isFailure)
    }
}
