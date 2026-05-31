package com.dev.hotel.booking.detail

import com.dev.utils.uitext.UiText
import com.example.domain.model.hotel.booking.BookingDetails

sealed interface BookingDetailUiState {
    data object Loading : BookingDetailUiState
    data class Success(
        val details: BookingDetails,
        val isCancelling: Boolean = false
    ) : BookingDetailUiState
    data class Error(val message: UiText) : BookingDetailUiState
}
