package com.dev.hotel.booking.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.example.domain.usecase.hotel.GetUserBookingsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingListViewModel @Inject constructor(
    private val getUserBookingsUseCase: GetUserBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookingListUiState>(BookingListUiState.Loading)
    val uiState: StateFlow<BookingListUiState> = _uiState.asStateFlow()

    private val _event = Channel<BookingListEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadBookings()
    }

    fun loadBookings() {
        _uiState.value = BookingListUiState.Loading
        viewModelScope.launch {
            when (val result = getUserBookingsUseCase()) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = BookingListUiState.Empty
                    } else {
                        _uiState.value = BookingListUiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    val errorMessage = when (result.error) {
                        is DataError.Network -> UiText.DynamicString("Network error. Please check your connection.")
                        DataError.Data.NotFound -> UiText.DynamicString("Bookings not found.")
                        is DataError.Logical -> UiText.DynamicString((result.error as DataError.Logical).message ?: "Error")
                        else -> UiText.DynamicString("An unexpected error occurred.")
                    }
                    _uiState.value = BookingListUiState.Error(errorMessage)
                    _event.send(BookingListEvent.ShowError(errorMessage))
                }
            }
        }
    }

    fun onBookingClicked(reference: String) {
        viewModelScope.launch {
            _event.send(BookingListEvent.NavigateToDetail(reference))
        }
    }

    fun onExploreHotelsClicked() {
        viewModelScope.launch {
            _event.send(BookingListEvent.NavigateToHotels)
        }
    }
}
