package com.dev.hotel.presentation

import java.time.LocalDate

sealed interface HotelDetailAction {
    data object Retry : HotelDetailAction
    data class ToggleRoomExpansion(val roomCode: String) : HotelDetailAction
    data object CollapseDescription : HotelDetailAction
    data object ExpandDescription : HotelDetailAction
    data class BookRoom(val rateKey: String) : HotelDetailAction
    data object LoadReviews : HotelDetailAction
    data object BackClicked : HotelDetailAction
    data object FavoriteClicked : HotelDetailAction
    data class NearbyExploreClicked(val itemName: String) : HotelDetailAction
    data object BookNowClicked : HotelDetailAction

    // Booking Card Actions
    data class OnCheckInDateSelected(val date: LocalDate) : HotelDetailAction
    data class OnCheckOutDateSelected(val date: LocalDate) : HotelDetailAction
    data class OnAdultsCountChanged(val count: Int) : HotelDetailAction
    data class OnChildrenCountChanged(val count: Int) : HotelDetailAction
    data class OnChildAgeChanged(val index: Int, val age: Int) : HotelDetailAction
    data class OnRoomsCountChanged(val count: Int) : HotelDetailAction
    data object CheckAvailabilityClicked : HotelDetailAction
}
