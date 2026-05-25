package com.dev.hotel.search

import com.example.domain.model.hotel.Occupancy
import java.time.LocalDate

sealed interface HotelSearchAction {
    data class OnDestinationChanged(val destination: String) : HotelSearchAction
    data class OnCheckInSelected(val date: LocalDate?) : HotelSearchAction
    data class OnCheckOutSelected(val date: LocalDate?) : HotelSearchAction
    data class OnOccupancyChanged(val occupancies: List<Occupancy>) : HotelSearchAction
    data class OnGuestSheetVisibilityChanged(val visible: Boolean) : HotelSearchAction
    data object SearchClicked : HotelSearchAction
    data object RetryClicked : HotelSearchAction
    data class HotelClicked(val hotelCode: Int) : HotelSearchAction
    data object BackClicked : HotelSearchAction
}
