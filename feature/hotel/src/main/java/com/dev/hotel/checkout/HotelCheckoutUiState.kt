package com.dev.hotel.checkout

import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.utils.hotel.HotelCheckoutValidationError

data class HotelCheckoutUiState(
    val rateKey: String = "",
    val hotelCode: Int = 0,
    val checkIn: String = "",
    val checkOut: String = "",
    val adultsCount: Int = 0,
    val childrenCount: Int = 0,
    val childrenAges: List<Int> = emptyList(),

    // Hotel details loaded from API
    val hotelState: UiState<com.example.domain.model.hotel.HotelDetails> = UiState.Loading,
    val selectedRatePrice: Double? = null,
    val selectedRateCurrency: String? = null,

    // Input fields
    val holderFirstName: String = "",
    val holderLastName: String = "",
    val remark: String = "",

    // Rooms & Guests info
    val rooms: List<HotelBookingRoom> = emptyList(),

    // UI flags
    val isSubmitting: Boolean = false,
    val isPaymentProcessing: Boolean = false,
    val validationErrors: List<HotelCheckoutValidationError> = emptyList(),
    val errorMessage: String? = null
)
