package com.dev.hotel.checkout

sealed interface BookingSuccessUiEvent {
    data object NavigateToHome : BookingSuccessUiEvent
}
