package com.dev.hotel.presentation

import com.dev.utils.uitext.UiText

sealed interface HotelDetailEvent {
    data object NavigateBack : HotelDetailEvent
    data object NavigateToReviews : HotelDetailEvent
    data class NavigateToNearbyDetails(val hotelCode: Int) : HotelDetailEvent
    data class NavigateToBooking(val rateKey: String?) : HotelDetailEvent
    data class ShowMessage(val message: UiText) : HotelDetailEvent
}
