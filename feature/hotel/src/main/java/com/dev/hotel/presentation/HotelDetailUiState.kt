package com.dev.hotel.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.model.hotel.NearbyHotel
import java.time.LocalDate

data class HotelDetailUiState(
    val hotelState: UiState<HotelDetails> = UiState.Loading,
    val nearbyHotelsState: UiState<List<NearbyHotel>> = UiState.Idle,
    val expandedRoomCodes: Set<String> = emptySet(),
    val isDescriptionExpanded: Boolean = false,
    val isFavorite: Boolean = false,

    // Booking Card State
    val checkInDate: LocalDate = LocalDate.now().plusDays(1),
    val checkOutDate: LocalDate = LocalDate.now().plusDays(2),
    val adults: Int = 2,
    val children: Int = 0,
    val childrenAges: List<Int> = emptyList(),
    val rooms: Int = 1,
    val isSearchingRooms: Boolean = false
)
