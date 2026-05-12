
package com.example.feature.booking.presentation

import com.example.feature.booking.presentation.BookingViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.Passenger
import com.example.domain.model.booking.PaymentIntentInfo
import com.example.domain.repository.booking.BookingRepository
import com.example.domain.usecase.booking.ConfirmFlightOrderUseCase
import com.example.domain.usecase.booking.CreatePaymentIntentUseCase
import com.example.domain.usecase.booking.ValidatePassengersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelBookingFailureTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: BookingViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val fakeRepo = object : BookingRepository {
            override suspend fun createPaymentIntent(offerId: String, passengers: List<Passenger>) = Result.success(PaymentIntentInfo("secret", "pi_123"))
            override suspend fun confirmFlightOrder(request: BookingRequest) = Result.failure<BookingResult>(Exception("Booking failed"))
        }

        val validatePassengersUseCase = ValidatePassengersUseCase()
        val createPaymentIntentUseCase = CreatePaymentIntentUseCase(fakeRepo)
        val confirmFlightOrderUseCase = ConfirmFlightOrderUseCase(fakeRepo)
        
        val dummyPayload = com.example.domain.model.flights.details.FlightDetailsPayload(
            offerId = "off_123",
            totalPrice = 100.0,
            taxAmount = 10.0,
            currency = "USD",
            totalDuration = "2h",
            checkedBags = 1,
            isRefundable = true,
            refundPenaltyAmount = 0.0,
            pricePerPerson = 100.0,
            segments = emptyList()
        )
        
        val fakeFlightDetailsRepo = object : com.example.domain.repository.flights.FlightDetailsRepository {
            override suspend fun getFlightDetails(offerId: String, forceRefresh: Boolean): com.example.domain.utils.Result<com.example.domain.model.flights.details.FlightDetailsPayload, com.example.domain.utils.DataError> = com.example.domain.utils.Result.Success(dummyPayload)
        }
        val getFlightDetailsUseCase = com.example.domain.usecase.flights.GetFlightDetailsUseCase(fakeFlightDetailsRepo)

        viewModel = BookingViewModel(
            validatePassengersUseCase,
            createPaymentIntentUseCase,
            confirmFlightOrderUseCase,
            getFlightDetailsUseCase,
            SavedStateHandle(mapOf("offerId" to "off_123"))
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given booking fails after payment then error is set`() {
        // Set valid passenger to pass validation
        viewModel.onPassengerUpdated(0, com.example.domain.model.booking.Passenger("Mr", "John", "Doe", "1990-01-01", "john@example.com", "123456789", "Male"))
        
        // Start booking flow to create payment intent
        viewModel.onBookNow()
        
        // Mock successful payment result from Stripe
        viewModel.onPaymentResult(success = true)
        
        assertEquals("Booking failed", viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isProcessing)
    }
}
