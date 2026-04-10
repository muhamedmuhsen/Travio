package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.favorite.toPlace
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationDetailViewModel @Inject constructor(
    private val getDestinationByIdUseCase: GetDestinationByIdUseCase,
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val favoritePlaceUseCase: FavoritePlaceUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val destinationId: Int =
        savedStateHandle.get<Int>("id") ?: savedStateHandle.get<String>("id")?.toIntOrNull() ?: -1

    private val _uiState = MutableStateFlow(DestinationDetailUiState())
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DestinationDetailEvent>()
    val events = _events.asSharedFlow()

    init {
        loadDestination()
    }

    private fun loadDestination() {
        if (destinationId == -1) {
            _uiState.value = _uiState.value.copy(detailState = UiState.Error("Destination not found"))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(detailState = UiState.Loading)

            when (val result = getDestinationByIdUseCase(destinationId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(detailState = UiState.Success(result.data))
                    loadRelatedDestinations(result.data)
                }
                is Result.Error -> {
                    // Handling Not Found vs Error later (Task US3), but for now generic Error
                    _uiState.value = _uiState.value.copy(detailState = UiState.Error("Could not load destination details"))
                }
            }
        }
    }

    private fun loadRelatedDestinations(destination: com.example.domain.model.destination.Destination) {
        val interestId = destination.interests.firstOrNull()?.interestID ?: return // If no interest, skip

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(relatedDestinationsState = UiState.Loading)
            // Fetch a larger page size (e.g., 30) so we have a bigger pool to shuffle from
            when (val result = getAllDestinationsUseCase(pageIndex = 1, pageSize = 30, cityId = null, interestId = interestId)) {
                is Result.Success -> {
                    // Filter out the current destination, shuffle the results to make it dynamic, and take the top 10
                    val filtered = result.data
                        .filter { it.destinationID != destination.destinationID }
                        .shuffled()
                        .take(10)
                    _uiState.value = _uiState.value.copy(relatedDestinationsState = UiState.Success(filtered))
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(relatedDestinationsState = UiState.Error("Failed to load related destinations"))
                }
            }
        }
    }

    fun onAction(action: DestinationDetailAction) {
        when (action) {
            is DestinationDetailAction.OnBackClicked -> {
                viewModelScope.launch { _events.emit(DestinationDetailEvent.NavigateBack) }
            }
            is DestinationDetailAction.OnRetry -> {
                loadDestination()
            }
            is DestinationDetailAction.OnFavoriteClicked -> toggleFavorite()
            is DestinationDetailAction.OnViewOnMapClicked -> openMap()
            is DestinationDetailAction.OnShareClicked -> shareMap()
            // Add other actions
            else -> {}
        }
    }

    private fun toggleFavorite() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                val newState = !_uiState.value.isFavorite
                _uiState.value = _uiState.value.copy(isFavorite = newState)

                when (val result = favoritePlaceUseCase(destination.toPlace())) {
                    is Result.Success -> {
                        val message = if (newState) "Saved to favourites" else "Removed from favourites"
                        _events.emit(DestinationDetailEvent.ShowSuccessSnackbar(message))
                    }
                    is Result.Error -> {
                        // Revert
                        _uiState.value = _uiState.value.copy(isFavorite = !newState)
                        _events.emit(DestinationDetailEvent.ShowErrorSnackbar("Action failed"))
                    }
                }
            }
        }
    }

    private fun openMap() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                _events.emit(DestinationDetailEvent.OpenMap(destination.latitude, destination.longitude))
            }
        }
    }

    private fun shareMap() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                _events.emit(DestinationDetailEvent.ShareDestination("Check out ${destination.name} in ${destination.cityName}!"))
            }
        }
    }
}
