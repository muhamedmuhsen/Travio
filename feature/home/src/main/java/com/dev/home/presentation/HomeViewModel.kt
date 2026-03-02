package com.dev.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mapper.place.toPlace
import com.example.domain.model.destination.Destination
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import ui.state.UiState
import ui.text.UiText
import ui.text.asUiText
import javax.inject.Inject
import com.example.designsystem.R as DesignSystemR

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val getNearbyDestinationsUseCase: GetNearbyDestinationsUseCase,
    private val getFamousCountriesUseCase: GetFamousCountriesUseCase,
    private val favoritePlaceUseCase: FavoritePlaceUseCase,
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    private val getRecentlyViewedUseCase: GetRecentlyViewedUseCase,
    private val addToRecentlyViewedUseCase: AddToRecentlyViewedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Channel.UNLIMITED prevents event loss when multiple errors arrive in quick succession.
    private val _event = Channel<HomeEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    init {
        loadHomeData()
        observeFavoriteIds()
        observeRecentlyViewed()
        requestLocationPermission()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnDestinationClicked -> navigateToDestination(action.id)
            HomeAction.OnSearchClicked -> navigateToSearch()
            is HomeAction.OnFavoriteClicked -> toggleFavorite(action.destination)
            is HomeAction.OnSearchQueryChanged -> _uiState.update { it.copy(searchQuery = action.query) }
            is HomeAction.OnRetrySection -> retrySection(action.section)
            is HomeAction.OnLocationPermissionResult -> onLocationPermissionResult(action.granted)
        }
    }

    private fun requestLocationPermission() {
        viewModelScope.launch {
            _event.send(HomeEvent.RequestLocationPermission)
        }
    }

    private fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            loadNearbyDestinations()
        } else {
            _uiState.update {
                it.copy(
                    nearbyDestinationsState = UiState.Error(
                        UiText.StringResource(DesignSystemR.string.error_location_permission_denied)
                    )
                )
            }
        }
    }

    private fun retrySection(section: HomeSection) {
        when (section) {
            HomeSection.Countries -> loadFamousCountries()
            HomeSection.Recommended -> loadRecommendedDestinations()
            HomeSection.Nearby -> requestLocationPermission()
            HomeSection.RecentlyViewed -> observeRecentlyViewed()
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
            val place = destination.toPlace()
            val isCurrentlyFavorite = destination.destinationID in _uiState.value.favoriteIds
            when (val result = favoritePlaceUseCase(place)) {
                is Result.Success -> {
                    Timber.d(
                        "toggleFavorite: DB write succeeded for id=%d",
                        destination.destinationID
                    )
                    // favoriteIds is driven by the live DB flow — no manual update needed.
                    val message = if (isCurrentlyFavorite) {
                        UiText.StringResource(R.string.removed_from_favourites)
                    } else {
                        UiText.StringResource(R.string.saved_to_favourites)
                    }
                    _event.send(HomeEvent.ShowSuccessSnackbar(message))
                    // TODO: sync toggle with remote backend favourite endpoint
                }

                is Result.Error -> {
                    Timber.e("toggleFavorite: DB write failed — %s", result.error.name)
                    _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                }
            }
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToSearch) }
    }

    private fun navigateToDestination(id: String) {
        viewModelScope.launch {
            // Record the view using the destination already loaded in state.
            val destination = findDestinationById(id)
            if (destination != null) {
                addToRecentlyViewedUseCase(destination)
            }
            _event.send(HomeEvent.NavigateToDestination(id))
        }
    }

    /** Searches all loaded destination lists for a matching ID. */
    private fun findDestinationById(id: String): Destination? {
        val intId = id.toIntOrNull() ?: return null
        val s = _uiState.value
        val recommended =
            (s.recommendedDestinationsState as? UiState.Success)?.data.orEmpty()
        val nearby =
            (s.nearbyDestinationsState as? UiState.Success)?.data.orEmpty()
        return (recommended + nearby).firstOrNull { it.destinationID == intId }
    }

    private fun observeRecentlyViewed() {
        _uiState.update { it.copy(recentViewedDestinationsState = UiState.Loading) }
        viewModelScope.launch {
            getRecentlyViewedUseCase()
                .catch { e ->
                    Timber.e(e, "observeRecentlyViewed: failed")
                    _uiState.update {
                        it.copy(
                            recentViewedDestinationsState = UiState.Error(
                                UiText.StringResource(DesignSystemR.string.error_unknown)
                            )
                        )
                    }
                }
                .collect { destinations ->
                    _uiState.update {
                        it.copy(recentViewedDestinationsState = UiState.Success(destinations))
                    }
                }
        }
    }

    private fun loadHomeData() {
        loadRecommendedDestinations()
        loadFamousCountries()
    }

    private fun loadFamousCountries() {
        launchLoad(
            setLoading = { it.copy(countriesState = UiState.Loading) },
            setError = { state, msg -> state.copy(countriesState = UiState.Error(msg)) },
            setSuccess = { state, data -> state.copy(countriesState = UiState.Success(data)) },
            load = { getFamousCountriesUseCase() }
        )
    }

    private fun loadRecommendedDestinations() {
        launchLoad(
            setLoading = { it.copy(recommendedDestinationsState = UiState.Loading) },
            setError = { state, msg -> state.copy(recommendedDestinationsState = UiState.Error(msg)) },
            setSuccess = { state, data ->
                state.copy(recommendedDestinationsState = UiState.Success(data))
            },
            load = {
                getAllDestinationsUseCase(
                    pageIndex = 1,
                    pageSize = 10,
                    // TODO: derive from user preferences
                    cityId = 1,
                    // TODO: derive from user preferences
                    interestId = 1
                )
            }
        )
    }

    private fun loadNearbyDestinations() {
        launchLoad(
            setLoading = { it.copy(nearbyDestinationsState = UiState.Loading) },
            setError = { state, msg -> state.copy(nearbyDestinationsState = UiState.Error(msg)) },
            setSuccess = { state, data -> state.copy(nearbyDestinationsState = UiState.Success(data)) },
            load = { getNearbyDestinationsUseCase() }
        )
    }

    /**
     * Generic loader that eliminates the boilerplate shared by all three section loaders:
     * set Loading → launch → on Error set Error + send event → on Success set Success.
     *
     * @param setLoading  Produces a new state with the section in Loading.
     * @param setError    Produces a new state with the section in Error.
     * @param setSuccess  Produces a new state with the section in Success.
     * @param load        The suspending network/DB call that returns a [Result].
     */
    private fun <T> launchLoad(
        setLoading: (HomeUiState) -> HomeUiState,
        setError: (HomeUiState, UiText) -> HomeUiState,
        setSuccess: (HomeUiState, T) -> HomeUiState,
        load: suspend () -> Result<T, DataError>
    ) {
        _uiState.update(setLoading)
        viewModelScope.launch {
            when (val result = load()) {
                is Result.Error -> {
                    Timber.e("error occurred: %s", result.error)
                    val msg = result.error.asUiText()
                    _uiState.update { setError(it, msg) }
                    _event.send(HomeEvent.ShowErrorSnackbar(msg))
                }

                is Result.Success -> _uiState.update { setSuccess(it, result.data) }
            }
        }
    }
}
