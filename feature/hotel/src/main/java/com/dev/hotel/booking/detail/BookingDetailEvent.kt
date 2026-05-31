package com.dev.hotel.booking.detail

import com.dev.utils.uitext.UiText

sealed interface BookingDetailEvent {
    data class ShowError(val message: UiText) : BookingDetailEvent
    data class CancelSuccess(val message: UiText) : BookingDetailEvent
    data object NavigateToLogin : BookingDetailEvent
}
