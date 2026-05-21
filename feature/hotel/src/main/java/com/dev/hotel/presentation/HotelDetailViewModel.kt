package com.dev.hotel.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.common.navigation.Screen
import com.example.domain.usecase.hotel.GetHotelDetailsUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hotelCode: Int = checkNotNull(savedStateHandle[Screen.HotelDetailScreen.ARG_HOTEL_CODE])

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    val uiState: StateFlow<HotelDetailUiState> = _uiState.asStateFlow()

    init {
        loadHotelDetails()
    }

    fun onAction(action: HotelDetailAction) {
        when (action) {
            is HotelDetailAction.Retry -> loadHotelDetails()
            is HotelDetailAction.ToggleRoomExpansion -> toggleRoom(action.roomCode)
            is HotelDetailAction.ExpandDescription -> _uiState.update { it.copy(isDescriptionExpanded = true) }
            is HotelDetailAction.CollapseDescription -> _uiState.update { it.copy(isDescriptionExpanded = false) }
            is HotelDetailAction.BookRoom -> handleBookRoom(action.rateKey)
            is HotelDetailAction.LoadReviews -> { /* Implement if needed */ }
        }
    }

    private fun loadHotelDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(hotelState = UiState.Loading) }

            when (val result = getHotelDetailsUseCase(hotelCode = hotelCode)) {
                is Result.Success -> {
                    _uiState.update { it.copy(hotelState = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(hotelState = UiState.Error(result.error.asUiText())) }
                }
            }
        }
    }

    private fun toggleRoom(roomCode: String) {
        _uiState.update { state ->
            val newExpandedCodes = if (state.expandedRoomCodes.contains(roomCode)) {
                state.expandedRoomCodes - roomCode
            } else {
                state.expandedRoomCodes + roomCode
            }
            state.copy(expandedRoomCodes = newExpandedCodes)
        }
    }

    private fun handleBookRoom(rateKey: String) {
        // Will be implemented in future booking feature
    }
}
