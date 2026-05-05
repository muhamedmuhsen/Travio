package com.example.feature.booking.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.booking.BookingRequest
import com.example.domain.model.booking.Passenger
import com.example.domain.usecase.booking.ConfirmFlightOrderUseCase
import com.example.domain.usecase.booking.CreatePaymentIntentUseCase
import com.example.domain.usecase.booking.ValidatePassengersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val validatePassengersUseCase: ValidatePassengersUseCase,
    private val createPaymentIntentUseCase: CreatePaymentIntentUseCase,
    private val confirmFlightOrderUseCase: ConfirmFlightOrderUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val offerId: String = savedStateHandle.get<String>("offerId") ?: ""

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val _effect = Channel<BookingEffect>()
    val effect = _effect.receiveAsFlow()

    private var currentPaymentIntentId: String? = null

    fun onPassengerUpdated(
        index: Int,
        passenger: Passenger
    ) {
        _uiState.update { state ->
            val newList = state.passengers.toMutableList()
            if (index in newList.indices) {
                newList[index] = passenger
            }
            state.copy(passengers = newList)
        }
    }

    fun onAddPassenger() {
        _uiState.update { state ->
            state.copy(passengers = state.passengers + Passenger("", "", "", "", "", "", ""))
        }
    }

    fun onRemovePassenger(index: Int) {
        _uiState.update { state ->
            val newList = state.passengers.toMutableList()
            if (newList.size > 1 && index in newList.indices) {
                newList.removeAt(index)
            }
            state.copy(passengers = newList)
        }
    }

    fun onBookNow() {
        val passengers = _uiState.value.passengers
        val validationErrors = validatePassengersUseCase(passengers)

        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = validationErrors, error = "Please fix validation errors") }
            return
        }

        _uiState.update { it.copy(validationErrors = emptyMap()) }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, paymentStatus = PaymentStatus.CreatingIntent, error = null) }

            createPaymentIntentUseCase(offerId, passengers)
                .onSuccess { intentInfo ->
                    currentPaymentIntentId = intentInfo.paymentIntentId
                    _uiState.update { it.copy(paymentStatus = PaymentStatus.AwaitingConfirmation) }
                    _effect.send(BookingEffect.ConfirmPayment(intentInfo.clientSecret))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isProcessing = false, paymentStatus = PaymentStatus.Failed, error = error.message) }
                }
        }
    }

    fun onPaymentResult(
        success: Boolean,
        canceled: Boolean = false
    ) {
        if (success) {
            _uiState.update { it.copy(paymentStatus = PaymentStatus.Success) }
            confirmBooking()
        } else if (canceled) {
            _uiState.update { it.copy(isProcessing = false, paymentStatus = PaymentStatus.Canceled) }
        } else {
            _uiState.update { it.copy(isProcessing = false, paymentStatus = PaymentStatus.Failed, error = "Payment failed") }
        }
    }

    private fun confirmBooking() {
        val paymentIntentId = currentPaymentIntentId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val request = BookingRequest(
                offerId = offerId,
                passengers = _uiState.value.passengers,
                paymentIntentId = paymentIntentId
            )

            confirmFlightOrderUseCase(request)
                .onSuccess { result ->
                    _uiState.update { it.copy(isProcessing = false, bookingResult = result) }
                    _effect.send(BookingEffect.NavigateToConfirmation(result.pnr))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isProcessing = false, error = error.message) }
                }
        }
    }
}
