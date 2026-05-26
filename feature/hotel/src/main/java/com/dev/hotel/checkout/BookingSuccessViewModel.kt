package com.dev.hotel.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingSuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingSuccessUiState())
    val uiState: StateFlow<BookingSuccessUiState> = _uiState.asStateFlow()

    private val _event = Channel<BookingSuccessUiEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        val bookingId = savedStateHandle.get<String>("bookingId").orEmpty()
        val hotelName = savedStateHandle.get<String>("hotelName").orEmpty()
        val checkIn = savedStateHandle.get<String>("checkIn").orEmpty()
        val checkOut = savedStateHandle.get<String>("checkOut").orEmpty()

        _uiState.value = BookingSuccessUiState(
            bookingId = bookingId,
            hotelName = hotelName,
            checkIn = checkIn,
            checkOut = checkOut
        )
    }

    fun onAction(action: BookingSuccessAction) {
        when (action) {
            BookingSuccessAction.DoneClicked -> {
                viewModelScope.launch {
                    _event.send(BookingSuccessUiEvent.NavigateToHome)
                }
            }
        }
    }
}
