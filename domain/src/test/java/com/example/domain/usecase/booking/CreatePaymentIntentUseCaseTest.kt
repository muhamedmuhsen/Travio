package com.example.domain.usecase.booking

import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatePaymentIntentUseCaseTest {

    private val fakeRepository = object : BookingRepository {
        override suspend fun createPaymentIntent(offerId: String): Result<PaymentIntentInfo> {
            return if (offerId == "valid") {
                Result.success(PaymentIntentInfo("secret", "pi_123"))
            } else {
                Result.failure(Exception("Error"))
            }
        }

        override suspend fun confirmFlightOrder(request: BookingRequest): Result<BookingResult> {
            TODO("Not needed for this test")
        }
    }

    private val useCase = CreatePaymentIntentUseCase(fakeRepository)

    @Test
    fun `given valid offerId then returns success`() = runTest {
        val result = useCase("valid")
        assertTrue(result.isSuccess)
        assertEquals("pi_123", result.getOrNull()?.paymentIntentId)
    }

    @Test
    fun `given invalid offerId then returns failure`() = runTest {
        val result = useCase("invalid")
        assertTrue(result.isFailure)
    }
}
