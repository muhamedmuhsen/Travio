package com.dev.hotel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asErrorUiText
import com.example.domain.model.hotel.Occupancy
import com.example.domain.usecase.hotel.SearchHotelsUseCase
import com.example.feature.hotel.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HotelSearchViewModel @Inject constructor(
    private val searchHotelsUseCase: SearchHotelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelSearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<HotelSearchEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: HotelSearchAction) {
        when (action) {
            is HotelSearchAction.OnDestinationChanged -> {
                _uiState.update {
                    it.copy(
                        destination = action.destination,
                        destinationError = null
                    )
                }
            }
            is HotelSearchAction.OnCheckInSelected -> {
                val today = LocalDate.now()
                val selectedCheckIn = action.date ?: today
                val finalCheckIn = if (selectedCheckIn.isBefore(today)) today else selectedCheckIn

                _uiState.update { state ->
                    val finalCheckOut = state.checkOutDate?.let { currentCheckOut ->
                        if (!currentCheckOut.isAfter(finalCheckIn)) {
                            finalCheckIn.plusDays(1)
                        } else {
                            currentCheckOut
                        }
                    } ?: finalCheckIn.plusDays(1)

                    state.copy(
                        checkInDate = finalCheckIn,
                        checkOutDate = finalCheckOut,
                        dateError = null
                    )
                }
            }
            is HotelSearchAction.OnCheckOutSelected -> {
                _uiState.update { state ->
                    val checkIn = state.checkInDate ?: LocalDate.now()
                    val selectedCheckOut = action.date ?: checkIn.plusDays(1)

                    if (!selectedCheckOut.isAfter(checkIn)) {
                        state.copy(
                            checkOutDate = selectedCheckOut,
                            dateError = "Check-out date must be after check-in date"
                        )
                    } else {
                        state.copy(
                            checkOutDate = selectedCheckOut,
                            dateError = null
                        )
                    }
                }
            }
            is HotelSearchAction.OnOccupancyChanged -> {
                _uiState.update {
                    it.copy(
                        occupancies = action.occupancies
                    )
                }
            }
            is HotelSearchAction.OnGuestSheetVisibilityChanged -> {
                _uiState.update {
                    it.copy(
                        isGuestSheetVisible = action.visible
                    )
                }
            }
            is HotelSearchAction.SearchClicked -> {
                performSearch()
            }
            is HotelSearchAction.RetryClicked -> {
                performSearch()
            }
            is HotelSearchAction.HotelClicked -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    val checkIn = state.checkInDate ?: LocalDate.now()
                    val checkOut = state.checkOutDate ?: checkIn.plusDays(1)
                    val mainOccupancy = state.occupancies.firstOrNull() ?: Occupancy(2, 0, emptyList())

                    val childrenAgesStr = if (mainOccupancy.childrenAges.isNotEmpty()) {
                        mainOccupancy.childrenAges.joinToString(",")
                    } else {
                        null
                    }

                    _events.send(
                        HotelSearchEvent.NavigateToHotelDetails(
                            hotelCode = action.hotelCode,
                            checkIn = checkIn,
                            checkOut = checkOut,
                            adults = mainOccupancy.adults,
                            children = mainOccupancy.children,
                            childrenAges = childrenAgesStr
                        )
                    )
                }
            }
            is HotelSearchAction.BackClicked -> {
                viewModelScope.launch {
                    _events.send(HotelSearchEvent.NavigateBack)
                }
            }
        }
    }

    private fun performSearch() {
        val currentState = _uiState.value

        // Validate destination
        if (currentState.destination.isBlank()) {
            _uiState.update { it.copy(destinationError = "Destination is required") }
            return
        }

        val checkIn = currentState.checkInDate ?: LocalDate.now()
        val checkOut = currentState.checkOutDate ?: checkIn.plusDays(1)

        if (!checkOut.isAfter(checkIn)) {
            _uiState.update { it.copy(dateError = "Check-out date must be after check-in date") }
            return
        }

        // Validate occupancies
        for (occupancy in currentState.occupancies) {
            if (occupancy.adults !in 1..6 || occupancy.children !in 0..4 || occupancy.childrenAges.size != occupancy.children) {
                viewModelScope.launch {
                    _events.send(
                        HotelSearchEvent.ShowValidationError(
                            UiText.StringResource(R.string.hotel_search_validation_invalid_occupancy)
                        )
                    )
                }
                return
            }
            if (occupancy.children > 0 && occupancy.childrenAges.any { it !in 0..17 }) {
                viewModelScope.launch {
                    _events.send(
                        HotelSearchEvent.ShowValidationError(
                            UiText.StringResource(R.string.hotel_search_validation_invalid_occupancy)
                        )
                    )
                }
                return
            }
        }

        // Perform search
        viewModelScope.launch {
            _uiState.update { it.copy(searchState = UiState.Loading) }
            when (val result = searchHotelsUseCase(currentState.destination, checkIn, checkOut, currentState.occupancies)) {
                is com.example.domain.utils.Result.Success -> {
                    _uiState.update {
                        it.copy(searchState = UiState.Success(result.data))
                    }
                }
                is com.example.domain.utils.Result.Error -> {
                    _uiState.update {
                        it.copy(searchState = UiState.Error(result.asErrorUiText()))
                    }
                }
            }
        }
    }
}
