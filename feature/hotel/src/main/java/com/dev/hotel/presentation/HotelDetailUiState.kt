package com.dev.hotel.presentation

import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.HotelDetails

data class HotelDetailUiState(
    val hotelState: UiState<HotelDetails> = UiState.Loading,
    val expandedRoomCodes: Set<String> = emptySet(),
    val isDescriptionExpanded: Boolean = false
)
