package com.dev.hotel.checkout

sealed interface HotelCheckoutUiEvent {
    data class LaunchPaymentSheet(val clientSecret: String, val bookingId: String) : HotelCheckoutUiEvent
    data class NavigateToSuccess(val bookingId: String) : HotelCheckoutUiEvent
    data class ShowError(val message: String) : HotelCheckoutUiEvent
    data object NavigateBack : HotelCheckoutUiEvent
}
