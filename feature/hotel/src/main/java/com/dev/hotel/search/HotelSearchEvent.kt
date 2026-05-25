package com.dev.hotel.search

import com.dev.utils.uitext.UiText
import java.time.LocalDate

sealed interface HotelSearchEvent {
    data class NavigateToHotelDetails(
        val hotelCode: Int,
        val checkIn: LocalDate,
        val checkOut: LocalDate,
        val adults: Int,
        val children: Int,
        val childrenAges: String?
    ) : HotelSearchEvent
    data class ShowValidationError(val message: UiText) : HotelSearchEvent
    data object NavigateBack : HotelSearchEvent
}
