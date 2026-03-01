package com.dev.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mapper.place.toPlace
import com.example.domain.model.destination.Destination
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val getNearbyDestinationsUseCase: GetNearbyDestinationsUseCase,
    private val getFamousCountriesUseCase: GetFamousCountriesUseCase,
    private val favoritePlaceUseCase: FavoritePlaceUseCase,
    private val getAllPlacesUseCase: GetAllPlacesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _event = Channel<HomeEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    // Prevents race conditions when the user taps the favourite button rapidly.
    private val favoriteMutex = Mutex()

    init {
        loadHomeData()
        observeFavoriteIds()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnDestinationClicked -> navigateToDestination(action.id)
            HomeAction.OnSearchClicked -> navigateToSearch()
            is HomeAction.OnFavoriteClicked -> toggleFavorite(action.destination)
            is HomeAction.OnSearchQueryChanged -> _uiState.update { it.copy(searchQuery = action.query) }
        }
    }


    private fun observeFavoriteIds() {
        viewModelScope.launch {
            getAllPlacesUseCase()
                .catch { e -> Timber.e(e, "observeFavoriteIds: failed to observe favorites") }
                .collect { places ->
                    _uiState.update { state ->
                        state.copy(favoriteIds = places.map { it.id }.toSet())
                    }
                }
        }
    }

    private fun toggleFavorite(destination: Destination) {
        viewModelScope.launch {
            // The mutex ensures that concurrent taps are serialized — the second tap
            // always sees the state written by the first tap before deciding to add/remove.
            favoriteMutex.withLock {
                val place = destination.toPlace()
                when (val result = favoritePlaceUseCase(place)) {
                    is Result.Success -> {
                        Timber.d(
                            "toggleFavorite: DB write succeeded for id=%d",
                            destination.destinationID
                        )
                        // favoriteIds is driven by the live DB flow — no manual update needed.
                        // TODO: sync toggle with remote backend favourite endpoint
                    }

                    is Result.Error -> {
                        Timber.e("toggleFavorite: DB write failed — %s", result.error.name)
                        _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                    }
                }
            }
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToSearch) }
    }

    private fun navigateToDestination(id: String) {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToDestination(id)) }
    }

    private fun loadHomeData() {
        loadRecommendedDestinations()
        loadNearbyDestinations()
        loadFamousCountries()
    }

    private fun loadFamousCountries() {
        _uiState.update { state -> state.copy(countriesState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = getFamousCountriesUseCase()) {
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(countriesState = UiState.Error(result.error.asUiText()))
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                }

                is Result.Success -> {
                    _uiState.update { state -> state.copy(countriesState = UiState.Success(result.data)) }
                }
            }
        }
    }

    private fun loadRecommendedDestinations() {
        _uiState.update { state -> state.copy(recommendedDestinationsState = UiState.Loading) }

        viewModelScope.launch {
            when (
                val result = getAllDestinationsUseCase(
                    pageIndex = 1,
                    pageSize = 10,
                    // TODO: derive from user preferences
                    cityId = 1,
                    // TODO: derive from user preferences
                    interestId = 1
                )
            ) {
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(recommendedDestinationsState = UiState.Error(result.error.asUiText()))
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                }

                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(recommendedDestinationsState = UiState.Success(result.data))
                    }
                }
            }
        }
    }

    private fun loadNearbyDestinations() {
        _uiState.update { state -> state.copy(nearbyDestinationsState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = getNearbyDestinationsUseCase()) {
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            nearbyDestinationsState = UiState.Error(
                                result.error.asUiText()
                            )
                        )
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                }

                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            nearbyDestinationsState = UiState.Success(
                                result.data
                            )
                        )
                    }
                }
            }
        }
    }
}
