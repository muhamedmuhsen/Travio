package com.dev.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.home.presentation.flights.FlightsSectionUiState
import com.dev.home.presentation.flights.toFlightCardContent
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asUiText
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.DestinationsPage
import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.GetDestinationsPageUseCase
import com.example.domain.usecase.destinations.GetFamousCountriesUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.usecase.destinations.GetRecentlyViewedUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import com.example.domain.usecase.hotel.GetNearbyHotelsUseCase
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
import javax.inject.Inject
import com.example.designsystem.R as DesignSystemR

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDestinationsPageUseCase: GetDestinationsPageUseCase,
    private val getNearbyDestinationsUseCase: GetNearbyDestinationsUseCase,
    private val getFamousCountriesUseCase: GetFamousCountriesUseCase,
    private val addDestinationFavoriteUseCase: AddDestinationFavoriteUseCase,
    private val removeDestinationFavoriteUseCase: RemoveDestinationFavoriteUseCase,
    private val observeFavoriteDestinationIdsUseCase: ObserveFavoriteDestinationIdsUseCase,
    private val getRecentlyViewedUseCase: GetRecentlyViewedUseCase,
    private val addToRecentlyViewedUseCase: AddToRecentlyViewedUseCase,
    private val getTopFlightOffersUseCase: GetTopFlightOffersUseCase,
    private val getNearbyHotelsUseCase: GetNearbyHotelsUseCase
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
            is HomeAction.OnHotelClicked -> {
                viewModelScope.launch {
                    _event.send(HomeEvent.NavigateToHotelDetails(action.code))
                }
            }
            is HomeAction.OnFlightCardClicked -> navigateToFlightDetails(action.id)
            is HomeAction.OnFlightCtaClicked -> startFlightBooking(action.id)
            HomeAction.OnSearchClicked -> navigateToSearch()
            is HomeAction.OnFavoriteClicked -> toggleFavorite(action.destination)
            is HomeAction.OnSearchQueryChanged -> _uiState.update { it.copy(searchQuery = action.query) }
            is HomeAction.OnRetrySection -> retrySection(action.section)
            HomeAction.OnLoadMoreDestinations -> loadMoreDestinations()
            HomeAction.OnRetryLoadMoreDestinations -> loadMoreDestinations(force = true)
            HomeAction.OnRefresh -> onRefresh()
            is HomeAction.OnDestinationItemVisible -> onDestinationItemVisible(action.index)
            is HomeAction.OnLocationPermissionResult -> onLocationPermissionResult(action.granted)
            HomeAction.OnSeeAllFlightsClicked -> navigateToSeeAllFlights()
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
            loadNearbyHotels()
        } else {
            _uiState.update {
                it.copy(
                    nearbyDestinationsState = UiState.Error(
                        UiText.StringResource(DesignSystemR.string.error_location_permission_denied)
                    ),
                    nearbyHotelsState = UiState.Error(
                        UiText.StringResource(DesignSystemR.string.error_location_permission_denied)
                    )
                )
            }
        }
    }

    private fun retrySection(section: HomeSection) {
        when (section) {
            HomeSection.Countries -> loadFamousCountries()
            HomeSection.Recommended,
            HomeSection.Destinations -> fetchDestinationPage(
                pageIndex = FIRST_PAGE,
                isInitialLoad = true
            )

            HomeSection.Nearby,
            HomeSection.NearbyHotels -> requestLocationPermission()
            HomeSection.RecentlyViewed -> observeRecentlyViewed()
            HomeSection.Flights -> loadFlightsSectionData(forceRefresh = false)
        }
    }

    private fun observeFavoriteIds() {
        viewModelScope.launch {
            observeFavoriteDestinationIdsUseCase()
                .catch { e -> Timber.e(e, "observeFavoriteIds(shared): failed") }
                .collect { favoriteIds ->
                    _uiState.update { it.copy(favoriteIds = favoriteIds) }
                }
        }
    }

    private fun toggleFavorite(destination: Destination) {
        viewModelScope.launch {
            val destinationId = destination.destinationID
            if (destinationId in _uiState.value.favoriteMutationInFlightIds) return@launch

            val isCurrentlyFavorite = destinationId in _uiState.value.favoriteIds
            if (!isCurrentlyFavorite) {
                _uiState.update {
                    it.copy(
                        favoriteIds = it.favoriteIds + destinationId,
                        favoriteMutationInFlightIds = it.favoriteMutationInFlightIds + destinationId
                    )
                }
                val addResult = addDestinationFavoriteUseCase(destinationId)

                when (addResult) {
                    is Result.Success -> {
                        val message = addResult.data.message
                            ?.takeIf { it.isNotBlank() }
                            ?.let(UiText::DynamicString)
                            ?: UiText.StringResource(R.string.saved_to_favourites)
                        _event.send(HomeEvent.ShowSuccessSnackbar(message))
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                favoriteIds = it.favoriteIds - destinationId
                            )
                        }
                        _event.send(HomeEvent.ShowErrorSnackbar(addResult.error.asUiText()))
                    }
                }
                _uiState.update {
                    it.copy(
                        favoriteMutationInFlightIds = it.favoriteMutationInFlightIds - destinationId
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    favoriteIds = it.favoriteIds - destinationId,
                    favoriteMutationInFlightIds = it.favoriteMutationInFlightIds + destinationId
                )
            }

            val removeResult = removeDestinationFavoriteUseCase(destinationId)

            when (removeResult) {
                is Result.Success -> {
                    val message = removeResult.data.message
                        ?.takeIf { it.isNotBlank() }
                        ?.let(UiText::DynamicString)
                        ?: UiText.StringResource(R.string.removed_from_favourites)
                    _event.send(HomeEvent.ShowSuccessSnackbar(message))
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(favoriteIds = it.favoriteIds + destinationId)
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(removeResult.error.asUiText()))
                }
            }

            _uiState.update {
                it.copy(
                    favoriteMutationInFlightIds = it.favoriteMutationInFlightIds - destinationId
                )
            }
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToSearch) }
    }

    private fun navigateToSeeAllFlights() {
        viewModelScope.launch { _event.send(HomeEvent.NavigateToSeeAllFlights) }
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

    private fun navigateToFlightDetails(id: String) {
        viewModelScope.launch {
            _event.send(HomeEvent.NavigateToFlightDetails(id))
        }
    }

    private fun startFlightBooking(id: String) {
        viewModelScope.launch {
            _event.send(HomeEvent.StartFlightBooking(id))
        }
    }

    /** Searches all loaded destination lists for a matching ID. */
    private fun findDestinationById(id: String): Destination? {
        val intId = id.toIntOrNull() ?: return null
        val s = _uiState.value
        val recommended = s.loadedDestinations
        val nearby =
            (s.nearbyDestinationsState as? UiState.Success<List<Destination>>)?.data.orEmpty()
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
        fetchDestinationPage(pageIndex = FIRST_PAGE, isInitialLoad = true)
        loadFamousCountries()
        loadFlightsSectionData()
    }

    // In-memory cache of the last fetched domain offers so detail navigation can access full data
    private var lastTopFlightOffers: List<TopFlightOffer> = emptyList()

    /**
     * Find a previously-loaded TopFlightOffer by its offerId without performing a network call.
     */
    private fun findTopOfferById(offerId: String): TopFlightOffer? {
        return lastTopFlightOffers.firstOrNull { it.offerId == offerId }
    }

    private fun loadFlightsSectionData(forceRefresh: Boolean = false) {
        // Prevent duplicate concurrent loads.
        // We only allow proceeding if state is NOT Loading.
        if (_uiState.value.flightsState is FlightsSectionUiState.Loading) return

        _uiState.update { it.copy(flightsState = FlightsSectionUiState.Loading) }

        viewModelScope.launch {
            when (val result = getTopFlightOffersUseCase(forceRefresh = forceRefresh, limit = HOME_PREVIEW_LIMIT)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            flightsState = FlightsSectionUiState.Error(
                                UiText.StringResource(R.string.no_flight_offers_available)
                            )
                        )
                    }
                    Timber.w("HomeViewModel: failed to load top flight offers -> ${result.error}")
                }

                is Result.Success -> {
                    val offers = result.data
                    if (offers.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                flightsState = FlightsSectionUiState.Error(
                                    UiText.StringResource(R.string.no_flight_offers_available)
                                )
                            )
                        }
                        return@launch
                    }

                    // keep domain models for detail navigation
                    lastTopFlightOffers = offers

                    val cards = offers.map { offer ->
                        // Use existing mapper to convert domain model to FlightCardContent
                        offer.toFlightCardContent()
                    }

                    _uiState.update { it.copy(flightsState = FlightsSectionUiState.Success(cards)) }
                }
            }
        }
    }

    private fun loadFamousCountries() {
        launchLoad(
            setLoading = { it.copy(countriesState = UiState.Loading) },
            setError = { state, msg -> state.copy(countriesState = UiState.Error(msg)) },
            setSuccess = { state, data -> state.copy(countriesState = UiState.Success(data)) },
            load = { getFamousCountriesUseCase() }
        )
    }

    private fun fetchDestinationPage(
        pageIndex: Int,
        isInitialLoad: Boolean
    ) {
        if (!isInitialLoad && _uiState.value.destinationsPagination.isLoadingMore) return

        if (isInitialLoad) {
            _uiState.update {
                it.copy(
                    recommendedDestinationsState = UiState.Loading,
                    destinationsPagination = it.destinationsPagination.copy(
                        isLoadingMore = false,
                        loadMoreError = null
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    destinationsPagination = it.destinationsPagination.copy(
                        isLoadingMore = true,
                        loadMoreError = null
                    )
                )
            }
        }

        viewModelScope.launch {
            when (
                val result = getDestinationsPageUseCase(
                    pageIndex = pageIndex,
                    pageSize = PAGE_SIZE

                )
            ) {
                is Result.Success -> applyDestinationPage(
                    page = result.data,
                    replaceExisting = isInitialLoad
                )

                is Result.Error -> {
                    val msg = result.error.asUiText()
                    if (isInitialLoad) {
                        _uiState.update {
                            it.copy(
                                destinationsPagination = it.destinationsPagination.copy(
                                    isLoadingMore = false,
                                    loadMoreError = null
                                ),
                                recommendedDestinationsState = UiState.Error(msg)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                destinationsPagination = it.destinationsPagination.copy(
                                    isLoadingMore = false,
                                    loadMoreError = msg
                                )
                            )
                        }
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(msg))
                }
            }
        }
    }

    private fun applyDestinationPage(
        page: DestinationsPage,
        replaceExisting: Boolean
    ) {
        _uiState.update { state ->
            val mergedDestinations = if (replaceExisting) {
                page.items
            } else {
                (state.loadedDestinations + page.items)
                    .distinctBy { it.destinationID }
            }

            val hasMore = page.items.size >= PAGE_SIZE
            state.copy(
                loadedDestinations = mergedDestinations,
                destinationsPagination = state.destinationsPagination.copy(
                    currentPageIndex = page.pageIndex,
                    pageSize = page.pageSize,
                    totalCount = page.count,
                    hasMore = hasMore,
                    isLoadingMore = false,
                    loadMoreError = null
                ),
                recommendedDestinationsState = UiState.Success(mergedDestinations)
            )
        }
    }

    private fun loadMoreDestinations(force: Boolean = false) {
        val paginationState = _uiState.value.destinationsPagination
        if (!paginationState.hasMore) return
        if (!force && paginationState.isLoadingMore) return

        val nextPageIndex = if (paginationState.currentPageIndex > 0) {
            paginationState.currentPageIndex + 1
        } else {
            FIRST_PAGE
        }
        fetchDestinationPage(pageIndex = nextPageIndex, isInitialLoad = false)
    }

    private fun onRefresh() {
        viewModelScope.launch {
            val previousState = _uiState.value
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    loadedDestinations = emptyList(),
                    destinationsPagination = HomePaginationState(pageSize = PAGE_SIZE),
                    recommendedDestinationsState = UiState.Loading
                )
            }

            when (
                val result = getDestinationsPageUseCase(
                    pageIndex = FIRST_PAGE,
                    pageSize = PAGE_SIZE

                )
            ) {
                is Result.Success -> {
                    applyDestinationPage(page = result.data, replaceExisting = true)
                    loadFamousCountries()
                    observeRecentlyViewed()
                    requestLocationPermission()
                    // refresh offers as part of pull-to-refresh
                    loadFlightsSectionData(forceRefresh = true)
                }

                is Result.Error -> {
                    val fallbackState = if (previousState.loadedDestinations.isNotEmpty()) {
                        UiState.Success(previousState.loadedDestinations)
                    } else {
                        previousState.recommendedDestinationsState
                    }
                    _uiState.update {
                        it.copy(
                            loadedDestinations = previousState.loadedDestinations,
                            destinationsPagination = previousState.destinationsPagination.copy(
                                isLoadingMore = false,
                                loadMoreError = null
                            ),
                            recommendedDestinationsState = fallbackState
                        )
                    }
                    _event.send(HomeEvent.ShowErrorSnackbar(result.error.asUiText()))
                }
            }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun onDestinationItemVisible(index: Int) {
        val totalLoadedItems = _uiState.value.loadedDestinations.size
        if (totalLoadedItems == 0) return

        val remainingItems = totalLoadedItems - (index + 1)
        if (remainingItems <= PREFETCH_THRESHOLD) {
            loadMoreDestinations()
        }
    }

    private fun loadNearbyDestinations() {
        launchLoad(
            setLoading = { it.copy(nearbyDestinationsState = UiState.Loading) },
            setError = { state, msg -> state.copy(nearbyDestinationsState = UiState.Error(msg)) },
            setSuccess = { state, data -> state.copy(nearbyDestinationsState = UiState.Success(data)) },
            load = { getNearbyDestinationsUseCase() }
        )
    }

    private fun loadNearbyHotels() {
        launchLoad(
            setLoading = { it.copy(nearbyHotelsState = UiState.Loading) },
            setError = { state, msg -> state.copy(nearbyHotelsState = UiState.Error(msg)) },
            setSuccess = { state, data -> state.copy(nearbyHotelsState = UiState.Success(data)) },
            load = { getNearbyHotelsUseCase() }
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

    private companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 10
        const val PREFETCH_THRESHOLD = 3
        const val HOME_PREVIEW_LIMIT = 5
    }
}
