package com.dev.hotel.booking.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.example.common.navigation.Screen
import com.example.domain.usecase.hotel.CancelBookingUseCase
import com.example.domain.usecase.hotel.GetBookingDetailsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.hotel.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val getBookingDetailsUseCase: GetBookingDetailsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reference: String = checkNotNull(savedStateHandle[Screen.BookingDetailScreen.ARG_REFERENCE])
    private val listTotalPrice: String? = savedStateHandle[Screen.BookingDetailScreen.ARG_TOTAL_PRICE]
    private val listCurrency: String? = savedStateHandle[Screen.BookingDetailScreen.ARG_CURRENCY]

    private val _uiState = MutableStateFlow<BookingDetailUiState>(BookingDetailUiState.Loading)
    val uiState: StateFlow<BookingDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<BookingDetailEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadBookingDetails()
    }

    fun loadBookingDetails() {
        _uiState.value = BookingDetailUiState.Loading
        viewModelScope.launch {
            when (val result = getBookingDetailsUseCase(reference)) {
                is Result.Success -> {
                    var details = result.data
                    if (!listTotalPrice.isNullOrBlank() && !listCurrency.isNullOrBlank()) {
                        details = details.copy(
                            totalPrice = listTotalPrice.toDoubleOrNull() ?: details.totalPrice,
                            currency = listCurrency
                        )
                    }
                    _uiState.value = BookingDetailUiState.Success(details)
                }
                is Result.Error -> {
                    val errorMessage = when (val error = result.error) {
                        is DataError.Network -> UiText.StringResource(R.string.booking_network_error)
                        DataError.Data.NotFound -> UiText.StringResource(R.string.booking_not_found)
                        is DataError.Logical -> UiText.DynamicString(
                            error.message ?: ""
                        ).takeIf { error.message != null }
                            ?: UiText.StringResource(R.string.booking_unexpected_error)
                        else -> UiText.StringResource(R.string.booking_unexpected_error)
                    }
                    _uiState.value = BookingDetailUiState.Error(errorMessage)
                    _event.send(BookingDetailEvent.ShowError(errorMessage))
                }
            }
        }
    }

    fun cancelBooking() {
        val currentState = _uiState.value
        if (currentState is BookingDetailUiState.Success) {
            _uiState.value = currentState.copy(isCancelling = true)
            viewModelScope.launch {
                when (val result = cancelBookingUseCase(reference)) {
                    is Result.Success -> {
                        _uiState.value = currentState.copy(isCancelling = false)
                        _event.send(
                            BookingDetailEvent.CancelSuccess(
                                UiText.StringResource(R.string.booking_cancelled_success)
                            )
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = currentState.copy(isCancelling = false)
                        if (result.error == DataError.Authentication.UnauthorizedAccess) {
                            _event.send(BookingDetailEvent.NavigateToLogin)
                        } else {
                            val errorMessage = when (val error = result.error) {
                                is DataError.Logical -> UiText.DynamicString(
                                    error.message ?: ""
                                ).takeIf { error.message != null }
                                    ?: UiText.StringResource(R.string.unable_to_cancel_booking)
                                else -> UiText.StringResource(R.string.unable_to_cancel_booking)
                            }
                            _event.send(BookingDetailEvent.ShowError(errorMessage))
                        }
                    }
                }
            }
        }
    }
}
