package com.dev.hotel.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asUiText
import com.example.common.navigation.Screen
import com.example.domain.usecase.hotel.GetHotelDetailsUseCase
import com.example.domain.usecase.hotel.GetNearbyHotelsUseCase
import com.example.domain.utils.Result
import com.example.feature.hotel.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val getNearbyHotelsUseCase: GetNearbyHotelsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hotelCode: Int = checkNotNull(savedStateHandle[Screen.HotelDetailScreen.ARG_HOTEL_CODE])

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    val uiState: StateFlow<HotelDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<HotelDetailEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

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
            is HotelDetailAction.LoadReviews -> {
                viewModelScope.launch { _event.send(HotelDetailEvent.NavigateToReviews) }
            }
            HotelDetailAction.BackClicked -> {
                viewModelScope.launch { _event.send(HotelDetailEvent.NavigateBack) }
            }
            HotelDetailAction.FavoriteClicked -> toggleFavorite()
            HotelDetailAction.CheckAvailabilityClicked -> searchRooms()
            is HotelDetailAction.NearbyExploreClicked -> {
                viewModelScope.launch {
                    _event.send(HotelDetailEvent.NavigateToNearbyDetails(action.itemName))
                }
            }
            HotelDetailAction.BookNowClicked -> {
                viewModelScope.launch {
                    _event.send(HotelDetailEvent.NavigateToBooking(null))
                }
            }
            is HotelDetailAction.OnCheckInDateSelected -> {
                _uiState.update { it.copy(checkInDate = action.date) }
                if (_uiState.value.checkOutDate.isBefore(action.date.plusDays(1))) {
                    _uiState.update { it.copy(checkOutDate = action.date.plusDays(1)) }
                }
            }
            is HotelDetailAction.OnCheckOutDateSelected -> {
                _uiState.update { it.copy(checkOutDate = action.date) }
            }
            is HotelDetailAction.OnAdultsCountChanged -> {
                _uiState.update { it.copy(adults = action.count) }
            }
            is HotelDetailAction.OnChildrenCountChanged -> {
                _uiState.update { state ->
                    val newAges = if (action.count > state.childrenAges.size) {
                        state.childrenAges + List(action.count - state.childrenAges.size) { 5 }
                    } else {
                        state.childrenAges.take(action.count)
                    }
                    state.copy(children = action.count, childrenAges = newAges)
                }
            }
            is HotelDetailAction.OnChildAgeChanged -> {
                _uiState.update { state ->
                    val newAges = state.childrenAges.toMutableList()
                    if (action.index in newAges.indices) {
                        newAges[action.index] = action.age
                    }
                    state.copy(childrenAges = newAges)
                }
            }
            is HotelDetailAction.OnRoomsCountChanged -> {
                _uiState.update { it.copy(rooms = action.count) }
            }
        }
    }

    private fun loadHotelDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(hotelState = UiState.Loading) }

            val result = getHotelDetailsUseCase(
                hotelCode = hotelCode,
                checkIn = _uiState.value.checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                checkOut = _uiState.value.checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                adults = _uiState.value.adults,
                children = _uiState.value.children,
                childrenAges = if (_uiState.value.children > 0) _uiState.value.childrenAges.joinToString(",") else null
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(hotelState = UiState.Success(result.data)) }
                    fetchNearbyHotels(result.data.latitude, result.data.longitude)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(hotelState = UiState.Error(result.error.asUiText())) }
                }
            }
        }
    }

    private fun fetchNearbyHotels(
        latitude: Double?,
        longitude: Double?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(nearbyHotelsState = UiState.Loading) }

            when (val result = getNearbyHotelsUseCase(latitude = latitude, longitude = longitude, radiusInKm = 10)) {
                is Result.Success -> {
                    val filteredHotels = result.data.filter { it.code != hotelCode }
                    _uiState.update { it.copy(nearbyHotelsState = UiState.Success(filteredHotels)) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(nearbyHotelsState = UiState.Error(result.error.asUiText())) }
                }
            }
        }
    }

    private fun searchRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearchingRooms = true) }

            val result = getHotelDetailsUseCase(
                hotelCode = hotelCode,
                checkIn = _uiState.value.checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                checkOut = _uiState.value.checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                adults = _uiState.value.adults,
                children = _uiState.value.children,
                childrenAges = if (_uiState.value.children > 0) _uiState.value.childrenAges.joinToString(",") else null
            )

            _uiState.update { it.copy(isSearchingRooms = false) }

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(hotelState = UiState.Success(result.data)) }
                    _event.send(HotelDetailEvent.ShowMessage(UiText.StringResource(R.string.hotel_details_rooms_updated)))
                }
                is Result.Error -> {
                    _event.send(HotelDetailEvent.ShowMessage(result.error.asUiText()))
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

    private fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        viewModelScope.launch {
            val message = if (_uiState.value.isFavorite) {
                UiText.StringResource(R.string.hotel_details_added_to_favorites)
            } else {
                UiText.StringResource(R.string.hotel_details_removed_from_favorites)
            }
            _event.send(HotelDetailEvent.ShowMessage(message))
        }
    }

    private fun handleBookRoom(rateKey: String) {
        viewModelScope.launch {
            _event.send(HotelDetailEvent.NavigateToBooking(rateKey))
        }
    }
}
