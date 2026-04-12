package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.common.navigation.DestinationDetailRoute
import com.example.domain.model.favorite.toPlace
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationDetailViewModel @Inject constructor(
    private val getDestinationByIdUseCase: GetDestinationByIdUseCase,
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val favoritePlaceUseCase: FavoritePlaceUseCase,
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val RELATED_PAGE_SIZE = 12
    }

    private val destinationId: Int? =
        runCatching { savedStateHandle.toRoute<DestinationDetailRoute>().id }.getOrNull()
            ?: savedStateHandle.get<Int>("id")

    private val _uiState = MutableStateFlow(DestinationDetailUiState())
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<DestinationDetailEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeFavoriteState()
        loadDestination()
    }

    private fun observeFavoriteState() {
        val currentDestinationId = destinationId ?: return

        viewModelScope.launch {
            getAllPlacesUseCase()
                .map { places -> places.any { it.id == currentDestinationId } }
                .distinctUntilChanged()
                .collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
        }
    }

    private fun loadDestination() {
        val currentDestinationId = destinationId
        if (currentDestinationId == null) {
            _uiState.value = _uiState.value.copy(detailState = UiState.Error("Destination not found"))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(detailState = UiState.Loading)

            when (val result = getDestinationByIdUseCase(currentDestinationId)) {
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
            when (
                val result = getAllDestinationsUseCase(
                    pageIndex = 1,
                    pageSize = RELATED_PAGE_SIZE,
                    cityId = null,
                    interestId = interestId
                )
            ) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        relatedDestinationsState = UiState.Success(
                            buildRelatedDestinations(result.data, destination.destinationID, interestId)
                        )
                    )
                }

                is Result.Error -> {
                    if (result.error == DataError.Network.Timeout) {
                        // Fallback: avoid blocking UI on slow category-filter endpoint.
                        when (
                            val fallback = getAllDestinationsUseCase(
                                pageIndex = 1,
                                pageSize = RELATED_PAGE_SIZE,
                                cityId = null,
                                interestId = null
                            )
                        ) {
                            is Result.Success -> {
                                _uiState.value = _uiState.value.copy(
                                    relatedDestinationsState = UiState.Success(
                                        buildRelatedDestinations(
                                            fallback.data,
                                            destination.destinationID,
                                            interestId
                                        )
                                    )
                                )
                            }

                            is Result.Error -> {
                                _uiState.value = _uiState.value.copy(
                                    relatedDestinationsState = UiState.Success(emptyList())
                                )
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            relatedDestinationsState = UiState.Error("Failed to load related destinations")
                        )
                    }
                }
            }
        }
    }

    private fun buildRelatedDestinations(
        source: List<com.example.domain.model.destination.Destination>,
        currentDestinationId: Int,
        interestId: Int
    ): List<com.example.domain.model.destination.Destination> {
        return source
            .filter { candidate ->
                candidate.destinationID != currentDestinationId &&
                    candidate.interests.any { it.interestID == interestId }
            }
            .sortedWith(
                compareByDescending<com.example.domain.model.destination.Destination> { it.rating }
                    .thenByDescending { it.totalReviews }
                    .thenBy { it.destinationID }
            )
            .take(10)
    }

    fun onAction(action: DestinationDetailAction) {
        when (action) {
            is DestinationDetailAction.OnBackClicked -> {
                viewModelScope.launch { _events.send(DestinationDetailEvent.NavigateBack) }
            }
            is DestinationDetailAction.OnRetry -> {
                loadDestination()
            }
            is DestinationDetailAction.OnFavoriteClicked -> toggleFavorite()
            is DestinationDetailAction.OnViewOnMapClicked -> openMap()
            is DestinationDetailAction.OnShareClicked -> shareMap()
            is DestinationDetailAction.OnImagePageChanged -> Unit
            is DestinationDetailAction.OnRetryRelatedDestinations -> retryRelatedDestinations()
            is DestinationDetailAction.OnRelatedDestinationClicked -> {
                viewModelScope.launch {
                    _events.send(DestinationDetailEvent.NavigateToDestination(action.destinationId))
                }
            }
        }
    }

    private fun toggleFavorite() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                val newState = !_uiState.value.isFavorite
                _uiState.value = _uiState.value.copy(isFavorite = newState)

                when (favoritePlaceUseCase(destination.toPlace())) {
                    is Result.Success -> {
                        val message = if (newState) "Saved to favourites" else "Removed from favourites"
                        _events.send(DestinationDetailEvent.ShowSuccessSnackbar(message))
                    }
                    is Result.Error -> {
                        // Revert
                        _uiState.value = _uiState.value.copy(isFavorite = !newState)
                        _events.send(DestinationDetailEvent.ShowErrorSnackbar("Action failed"))
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
                _events.send(DestinationDetailEvent.OpenMap(destination.latitude, destination.longitude))
            }
        }
    }

    private fun shareMap() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                _events.send(DestinationDetailEvent.ShareDestination("Check out ${destination.name} in ${destination.cityName}!"))
            }
        }
    }

    private fun retryRelatedDestinations() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            loadRelatedDestinations(detailState.data)
        }
    }
}
