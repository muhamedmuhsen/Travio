package com.dev.hotel.booking.list

import com.dev.utils.uitext.UiText
import com.example.domain.model.hotel.booking.BookingItem

sealed interface BookingListUiState {
    data object Loading : BookingListUiState
    data object Empty : BookingListUiState
    data class Success(val bookings: List<BookingItem>) : BookingListUiState
    data class Error(val message: UiText) : BookingListUiState
}
