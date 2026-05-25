package com.dev.hotel.search

import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.NearbyHotel
import com.example.domain.model.hotel.Occupancy
import java.time.LocalDate

data class HotelSearchUiState(
    val searchState: UiState<List<NearbyHotel>> = UiState.Idle,
    val destination: String = "",
    val checkInDate: LocalDate? = null,
    val checkOutDate: LocalDate? = null,
    val occupancies: List<Occupancy> = listOf(Occupancy(adults = 2, children = 0, childrenAges = emptyList())),
    val isGuestSheetVisible: Boolean = false,
    val destinationError: String? = null,
    val dateError: String? = null
)
