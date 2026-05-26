package com.dev.hotel.checkout

import androidx.lifecycle.SavedStateHandle
import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.model.hotel.HotelCheckoutResult
import com.example.domain.repository.hotel.HotelRepository
import com.example.domain.usecase.hotel.CheckoutHotelUseCase
import com.example.domain.usecase.hotel.ValidateHotelCheckoutUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.domain.utils.hotel.HotelCheckoutValidationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.domain.usecase.hotel.GetHotelDetailsUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotelCheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HotelCheckoutViewModel
    private lateinit var fakeRepository: FakeHotelRepository
    private lateinit var checkoutUseCase: CheckoutHotelUseCase
    private lateinit var validateUseCase: ValidateHotelCheckoutUseCase
    private lateinit var getHotelDetailsUseCase: GetHotelDetailsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeHotelRepository()
        checkoutUseCase = CheckoutHotelUseCase(fakeRepository)
        validateUseCase = ValidateHotelCheckoutUseCase()
        getHotelDetailsUseCase = GetHotelDetailsUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSavedStateHandle(
        rateKey: String = "rate_123",
        hotelCode: Int = 456,
        checkIn: String = "2026-06-15",
        checkOut: String = "2026-06-20",
        adults: Int = 2,
        children: Int = 1,
        childrenAges: String = "8"
    ): SavedStateHandle {
        return SavedStateHandle(
            mapOf(
                "rateKey" to rateKey,
                "hotelCode" to hotelCode,
                "checkIn" to checkIn,
                "checkOut" to checkOut,
                "adults" to adults,
                "children" to children,
                "childrenAges" to childrenAges
            )
        )
    }

    @Test
    fun `init parses arguments and initializes rooms with correct paxes`() = runTest {
        val savedState = createSavedStateHandle()
        viewModel = HotelCheckoutViewModel(getHotelDetailsUseCase, checkoutUseCase, validateUseCase, savedState)

        val state = viewModel.uiState.value
        assertEquals("rate_123", state.rateKey)
        assertEquals(456, state.hotelCode)
        assertEquals("2026-06-15", state.checkIn)
        assertEquals("2026-06-20", state.checkOut)
        assertEquals(2, state.adultsCount)
        assertEquals(1, state.childrenCount)
        assertEquals(listOf(8), state.childrenAges)

        // 1 room should be pre-created with 3 guests (2 adults, 1 child)
        assertEquals(1, state.rooms.size)
        val room = state.rooms[0]
        assertEquals("rate_123", room.rateKey)
        assertEquals(3, room.paxes.size)

        // Adult 1
        assertEquals("AD", room.paxes[0].type)
        assertEquals(null, room.paxes[0].age)
        // Adult 2
        assertEquals("AD", room.paxes[1].type)
        assertEquals(null, room.paxes[1].age)
        // Child 1
        assertEquals("CH", room.paxes[2].type)
        assertEquals(8, room.paxes[2].age)
    }

    @Test
    fun `update actions update input fields correctly`() = runTest {
        val savedState = createSavedStateHandle(adults = 1, children = 0)
        viewModel = HotelCheckoutViewModel(getHotelDetailsUseCase, checkoutUseCase, validateUseCase, savedState)

        viewModel.onAction(HotelCheckoutAction.UpdateHolderFirstName("Alice"))
        viewModel.onAction(HotelCheckoutAction.UpdateHolderLastName("Smith"))
        viewModel.onAction(HotelCheckoutAction.UpdateRemark("Late check-in please"))
        viewModel.onAction(HotelCheckoutAction.UpdatePaxName(roomIndex = 0, paxIndex = 0, name = "Bob"))
        viewModel.onAction(HotelCheckoutAction.UpdatePaxSurname(roomIndex = 0, paxIndex = 0, surname = "Jones"))

        val state = viewModel.uiState.value
        assertEquals("Alice", state.holderFirstName)
        assertEquals("Smith", state.holderLastName)
        assertEquals("Late check-in please", state.remark)
        assertEquals("Bob", state.rooms[0].paxes[0].name)
        assertEquals("Jones", state.rooms[0].paxes[0].surname)
    }

    @Test
    fun `submit with invalid inputs updates state with validation errors`() = runTest {
        val savedState = createSavedStateHandle(adults = 1, children = 0)
        viewModel = HotelCheckoutViewModel(getHotelDetailsUseCase, checkoutUseCase, validateUseCase, savedState)

        // Empty inputs
        viewModel.onAction(HotelCheckoutAction.SubmitCheckout)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.isNotEmpty())
        assertTrue(state.validationErrors.contains(HotelCheckoutValidationError.EmptyHolderFirstName))
        assertTrue(state.validationErrors.contains(HotelCheckoutValidationError.EmptyHolderLastName))
        assertTrue(state.validationErrors.contains(HotelCheckoutValidationError.EmptyPaxName(0, 0)))
    }

    @Test
    fun `submit with valid inputs calls usecase and emits LaunchPaymentSheet event`() = runTest {
        val savedState = createSavedStateHandle(adults = 1, children = 0)
        viewModel = HotelCheckoutViewModel(getHotelDetailsUseCase, checkoutUseCase, validateUseCase, savedState)

        viewModel.onAction(HotelCheckoutAction.UpdateHolderFirstName("Alice"))
        viewModel.onAction(HotelCheckoutAction.UpdateHolderLastName("Smith"))
        viewModel.onAction(HotelCheckoutAction.UpdatePaxName(0, 0, "Alice"))
        viewModel.onAction(HotelCheckoutAction.UpdatePaxSurname(0, 0, "Smith"))

        fakeRepository.checkoutResult = Result.Success(
            HotelCheckoutResult(
                clientSecret = "secret_abc",
                bookingId = "booking_123",
                totalPrice = 120.0,
                currency = "USD"
            )
        )

        viewModel.onAction(HotelCheckoutAction.SubmitCheckout)
        
        // Let flow collect event before we advance scheduler to idle
        val eventDeferred = async {
            viewModel.event.first()
        }

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationErrors.isEmpty())
        
        val event = eventDeferred.await()
        assertTrue(event is HotelCheckoutUiEvent.LaunchPaymentSheet)
        val launchEvent = event as HotelCheckoutUiEvent.LaunchPaymentSheet
        assertEquals("secret_abc", launchEvent.clientSecret)
        assertEquals("booking_123", launchEvent.bookingId)
    }

    @Test
    fun `payment actions update state and emit appropriate events`() = runTest {
        val savedState = createSavedStateHandle(adults = 1, children = 0)
        viewModel = HotelCheckoutViewModel(getHotelDetailsUseCase, checkoutUseCase, validateUseCase, savedState)

        // Payment completed
        val completeEventDeferred = async {
            viewModel.event.first()
        }
        viewModel.onAction(HotelCheckoutAction.PaymentCompleted("booking_123"))
        testDispatcher.scheduler.advanceUntilIdle()
        val completeEvent = completeEventDeferred.await()
        assertTrue(completeEvent is HotelCheckoutUiEvent.NavigateToSuccess)
        assertEquals("booking_123", (completeEvent as HotelCheckoutUiEvent.NavigateToSuccess).bookingId)

        // Payment failed
        val failedEventDeferred = async {
            viewModel.event.first()
        }
        viewModel.onAction(HotelCheckoutAction.PaymentFailed("Decline"))
        testDispatcher.scheduler.advanceUntilIdle()
        val failedEvent = failedEventDeferred.await()
        assertTrue(failedEvent is HotelCheckoutUiEvent.ShowError)
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Payment canceled
        viewModel.onAction(HotelCheckoutAction.PaymentCanceled)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    private class FakeHotelRepository : HotelRepository {
        var checkoutResult: Result<HotelCheckoutResult, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun searchNearbyHotels(
            latitude: Double,
            longitude: Double,
            checkIn: String,
            checkOut: String,
            radiusInKm: Int,
            maxHotels: Int,
            hotelCodes: List<Int>
        ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> = Result.Success(emptyList())

        override suspend fun searchHotels(
            destination: String,
            checkIn: String,
            checkOut: String,
            occupancies: List<com.example.domain.model.hotel.Occupancy>
        ): Result<List<com.example.domain.model.hotel.NearbyHotel>, DataError> = Result.Success(emptyList())

        override suspend fun getHotelDetails(
            hotelCode: Int, checkIn: String, checkOut: String, adults: Int,
            children: Int?, childrenAges: String?
        ): Result<com.example.domain.model.hotel.HotelDetails, DataError> = Result.Error(DataError.UnknownError)

        override suspend fun checkoutHotel(request: HotelCheckoutRequest): Result<HotelCheckoutResult, DataError> = checkoutResult
    }
}
