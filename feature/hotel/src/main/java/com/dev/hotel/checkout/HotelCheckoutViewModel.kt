package com.dev.hotel.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.model.hotel.HotelCheckoutRequest
import com.example.domain.usecase.hotel.CheckoutHotelUseCase
import com.example.domain.usecase.hotel.GetHotelDetailsUseCase
import com.example.domain.usecase.hotel.ValidateHotelCheckoutUseCase
import com.example.domain.utils.Result
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
class HotelCheckoutViewModel @Inject constructor(
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val checkoutHotelUseCase: CheckoutHotelUseCase,
    private val validateHotelCheckoutUseCase: ValidateHotelCheckoutUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelCheckoutUiState())
    val uiState: StateFlow<HotelCheckoutUiState> = _uiState.asStateFlow()

    private val _event = Channel<HotelCheckoutUiEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        val rateKeyStr = savedStateHandle.get<String>("rateKey").orEmpty()
        val rateKey = if (rateKeyStr.isNotEmpty()) java.net.URLDecoder.decode(rateKeyStr, "UTF-8") else ""
        val hotelCode = savedStateHandle.get<Int>("hotelCode") ?: 0
        val checkIn = savedStateHandle.get<String>("checkIn").orEmpty()
        val checkOut = savedStateHandle.get<String>("checkOut").orEmpty()
        val adults = savedStateHandle.get<Int>("adults") ?: 0
        val children = savedStateHandle.get<Int>("children") ?: 0
        val childrenAgesStr = savedStateHandle.get<String>("childrenAges").orEmpty()
        val childrenAges = if (childrenAgesStr.isNotEmpty()) {
            childrenAgesStr.split(",").mapNotNull { it.toIntOrNull() }
        } else {
            emptyList()
        }

        // Initialize Rooms & Paxes
        val initialPaxes = mutableListOf<HotelBookingPax>()
        // Pre-create adults
        for (i in 0 until adults) {
            initialPaxes.add(
                HotelBookingPax(
                    name = "",
                    surname = "",
                    type = "AD",
                    age = null,
                    roomId = 1
                )
            )
        }
        // Pre-create children
        for (i in 0 until children) {
            val age = childrenAges.getOrNull(i) ?: 5 // default fallback age
            initialPaxes.add(
                HotelBookingPax(
                    name = "",
                    surname = "",
                    type = "CH",
                    age = age,
                    roomId = 1
                )
            )
        }

        val initialRooms = listOf(
            HotelBookingRoom(
                rateKey = rateKey,
                paxes = initialPaxes
            )
        )

        _uiState.update { state ->
            state.copy(
                rateKey = rateKey,
                hotelCode = hotelCode,
                checkIn = checkIn,
                checkOut = checkOut,
                adultsCount = adults,
                childrenCount = children,
                childrenAges = childrenAges,
                rooms = initialRooms
            )
        }
        loadHotelDetails()
    }

    fun onAction(action: HotelCheckoutAction) {
        when (action) {
            is HotelCheckoutAction.UpdateHolderFirstName -> {
                _uiState.update { it.copy(holderFirstName = action.firstName, errorMessage = null) }
            }
            is HotelCheckoutAction.UpdateHolderLastName -> {
                _uiState.update { it.copy(holderLastName = action.lastName, errorMessage = null) }
            }
            is HotelCheckoutAction.UpdateRemark -> {
                _uiState.update { it.copy(remark = action.remark, errorMessage = null) }
            }
            is HotelCheckoutAction.UpdatePaxName -> {
                _uiState.update { state ->
                    val updatedRooms = state.rooms.mapIndexed { rIdx, room ->
                        if (rIdx == action.roomIndex) {
                            val updatedPaxes = room.paxes.mapIndexed { pIdx, pax ->
                                if (pIdx == action.paxIndex) {
                                    pax.copy(name = action.name)
                                } else {
                                    pax
                                }
                            }
                            room.copy(paxes = updatedPaxes)
                        } else {
                            room
                        }
                    }
                    state.copy(rooms = updatedRooms, errorMessage = null)
                }
            }
            is HotelCheckoutAction.UpdatePaxSurname -> {
                _uiState.update { state ->
                    val updatedRooms = state.rooms.mapIndexed { rIdx, room ->
                        if (rIdx == action.roomIndex) {
                            val updatedPaxes = room.paxes.mapIndexed { pIdx, pax ->
                                if (pIdx == action.paxIndex) {
                                    pax.copy(surname = action.surname)
                                } else {
                                    pax
                                }
                            }
                            room.copy(paxes = updatedPaxes)
                        } else {
                            room
                        }
                    }
                    state.copy(rooms = updatedRooms, errorMessage = null)
                }
            }
            is HotelCheckoutAction.UpdatePaxAge -> {
                _uiState.update { state ->
                    val updatedRooms = state.rooms.mapIndexed { rIdx, room ->
                        if (rIdx == action.roomIndex) {
                            val updatedPaxes = room.paxes.mapIndexed { pIdx, pax ->
                                if (pIdx == action.paxIndex) {
                                    pax.copy(age = action.age)
                                } else {
                                    pax
                                }
                            }
                            room.copy(paxes = updatedPaxes)
                        } else {
                            room
                        }
                    }
                    state.copy(rooms = updatedRooms, errorMessage = null)
                }
            }
            is HotelCheckoutAction.SubmitCheckout -> {
                submitCheckout()
            }
            is HotelCheckoutAction.PaymentCompleted -> {
                _uiState.update { it.copy(isPaymentProcessing = false, isSubmitting = false) }
                viewModelScope.launch {
                    _event.send(HotelCheckoutUiEvent.NavigateToSuccess(action.bookingId))
                }
            }
            is HotelCheckoutAction.PaymentFailed -> {
                _uiState.update { it.copy(isPaymentProcessing = false, errorMessage = action.error) }
                viewModelScope.launch {
                    _event.send(HotelCheckoutUiEvent.ShowError(action.error))
                }
            }
            is HotelCheckoutAction.PaymentCanceled -> {
                _uiState.update { it.copy(isPaymentProcessing = false, errorMessage = "Payment was cancelled") }
            }
            is HotelCheckoutAction.BackClicked -> {
                viewModelScope.launch {
                    _event.send(HotelCheckoutUiEvent.NavigateBack)
                }
            }
        }
    }

    private fun submitCheckout() {
        val currentState = _uiState.value
        val validationErrors = validateHotelCheckoutUseCase(
            holderFirstName = currentState.holderFirstName,
            holderLastName = currentState.holderLastName,
            rooms = currentState.rooms
        )

        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        _uiState.update { it.copy(validationErrors = emptyList(), isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val request = HotelCheckoutRequest(
                holderFirstName = currentState.holderFirstName,
                holderLastName = currentState.holderLastName,
                rooms = currentState.rooms,
                remark = currentState.remark.takeIf { it.isNotBlank() }
            )

            when (val result = checkoutHotelUseCase(request)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, isPaymentProcessing = true) }
                    _event.send(
                        HotelCheckoutUiEvent.LaunchPaymentSheet(
                            clientSecret = result.data.clientSecret,
                            bookingId = result.data.bookingId
                        )
                    )
                }
                is Result.Error -> {
                    val errorMsg = when (val error = result.error) {
                        is com.example.domain.utils.DataError.Logical -> error.message ?: "An unexpected logic error occurred."
                        else -> "Failed to submit booking. Please try again."
                    }
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = errorMsg) }
                    _event.send(HotelCheckoutUiEvent.ShowError(errorMsg))
                }
            }
        }
    }

    private fun loadHotelDetails() {
        val hotelCode = _uiState.value.hotelCode
        val checkIn = _uiState.value.checkIn
        val checkOut = _uiState.value.checkOut
        val adults = _uiState.value.adultsCount
        val children = _uiState.value.childrenCount
        val childrenAgesStr = if (children > 0) _uiState.value.childrenAges.joinToString(",") else null

        viewModelScope.launch {
            _uiState.update { it.copy(hotelState = UiState.Loading) }
            val result = getHotelDetailsUseCase(
                hotelCode = hotelCode,
                checkIn = checkIn,
                checkOut = checkOut,
                adults = adults,
                children = children,
                childrenAges = childrenAgesStr
            )
            when (result) {
                is Result.Success -> {
                    val rateKey = _uiState.value.rateKey
                    var selectedRatePrice: Double? = null
                    var selectedRateCurrency: String? = null

                    result.data.rooms.forEach { room ->
                        room.rates.forEach { rate ->
                            if (rate.rateKey == rateKey) {
                                selectedRatePrice = rate.price
                                selectedRateCurrency = result.data.currency
                            }
                        }
                    }

                    _uiState.update {
                        it.copy(
                            hotelState = UiState.Success(result.data),
                            selectedRatePrice = selectedRatePrice,
                            selectedRateCurrency = selectedRateCurrency
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(hotelState = UiState.Error(result.error.asUiText())) }
                }
            }
        }
    }
}
