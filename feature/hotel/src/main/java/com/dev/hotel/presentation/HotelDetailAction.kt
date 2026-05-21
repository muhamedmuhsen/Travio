package com.dev.hotel.presentation

sealed interface HotelDetailAction {
    data object Retry : HotelDetailAction
    data class ToggleRoomExpansion(val roomCode: String) : HotelDetailAction
    data object CollapseDescription : HotelDetailAction
    data object ExpandDescription : HotelDetailAction
    data class BookRoom(val rateKey: String) : HotelDetailAction
    data object LoadReviews : HotelDetailAction // From UI image
}
