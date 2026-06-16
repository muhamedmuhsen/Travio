package com.dev.hotel.booking.list

import com.dev.utils.uitext.UiText

sealed interface BookingListEvent {
    data class NavigateToDetail(val reference: String, val totalPrice: String, val currency: String) : BookingListEvent
    data object NavigateToHotels : BookingListEvent
    data class ShowError(val message: UiText) : BookingListEvent
}
