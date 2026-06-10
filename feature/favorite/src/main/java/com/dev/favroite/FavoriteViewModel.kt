package com.dev.favroite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.favroite.FavoritesTabUiState.Empty
import com.dev.favroite.FavoritesTabUiState.Error
import com.dev.favroite.FavoritesTabUiState.Loading
import com.dev.favroite.FavoritesTabUiState.Success
import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.domain.model.trip.FavoriteTripsPage
import com.example.domain.model.trip.TripSyncEvent
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.trip.GetFavoriteTripsUseCase
import com.example.domain.usecase.trip.ObserveTripSyncEventsUseCase
import com.example.domain.usecase.trip.ToggleFavoriteTripUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.favorite.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteTripsUseCase: GetFavoriteTripsUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase,
    private val addDestinationFavoriteUseCase: AddDestinationFavoriteUseCase,
    private val removeDestinationFavoriteUseCase: RemoveDestinationFavoriteUseCase,
    private val observeFavoriteDestinationIdsUseCase: com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase,
    private val getFavoriteSelectedTabUseCase: GetFavoriteSelectedTabUseCase,
    private val saveFavoriteSelectedTabUseCase: SaveFavoriteSelectedTabUseCase,
    private val getFavoriteDestinationsPageUseCase: GetFavoriteDestinationsPageUseCase,
    private val toggleFavoriteTripUseCase: ToggleFavoriteTripUseCase,
    private val observeTripSyncEventsUseCase: ObserveTripSyncEventsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()

    private val _effect = Channel<FavoriteEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var hasLoadedDestinations = false
    private var hasLoadedTrips = false
    private val queuedMutationIntents = mutableMapOf<Int, MutationIntent>()
    private val activeMutationIntents = mutableMapOf<Int, MutationIntent>()

    init {
        observeSharedFavoriteIds()
        observeTripSyncEvents()
        viewModelScope.launch {
            val restoredTab = mapToSectionTab(getFavoriteSelectedTabUseCase())
            _state.update { it.copy(selectedTab = restoredTab) }
            if (restoredTab != SectionTab.Destinations) {
                warmSharedFavoriteSync()
            }
            loadSelectedTabIfNeeded()
        }
    }

    private fun observeSharedFavoriteIds() {
        viewModelScope.launch {
            observeFavoriteDestinationIdsUseCase()
                .catch { throwable ->
                    System.err.println("FavoriteViewModel observer failed: ${throwable.message}")
                    _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_sync_unavailable)))
                }
                .collect { ids ->
                    _state.update { it.copy(favoriteIds = ids) }
                }
        }
    }

    private fun observeTripSyncEvents() {
        viewModelScope.launch {
            observeTripSyncEventsUseCase().collect { event ->
                val currentTrips = _state.value.loadedTrips
                val updatedTrips = when (event) {
                    is TripSyncEvent.FavoriteToggled -> {
                        if (!event.isFavorite) {
                            currentTrips.filter { it.id != event.tripId }
                        } else {
                            if (currentTrips.any { it.id == event.tripId }) {
                                currentTrips.map {
                                    if (it.id == event.tripId) it.copy(isFavorite = true) else it
                                }
                            } else {
                                fetchTripsPage(FIRST_PAGE, isInitialLoad = true)
                                return@collect
                            }
                        }
                    }
                    is TripSyncEvent.TripDeleted -> {
                        currentTrips.filter { it.id != event.tripId }
                    }
                }

                _state.update {
                    it.copy(
                        loadedTrips = updatedTrips,
                        tripsState = if (updatedTrips.isEmpty()) Empty else Success(updatedTrips)
                    )
                }
            }
        }
    }

    private fun warmSharedFavoriteSync() {
        viewModelScope.launch {
            getFavoriteDestinationsPageUseCase(pageIndex = FIRST_PAGE, pageSize = PAGE_SIZE)
        }
    }

    private fun mapToSectionTab(value: String?): SectionTab {
        return when (value) {
            SectionTab.Trips.name -> SectionTab.Trips
            else -> SectionTab.Destinations
        }
    }

    fun refreshData() {
        hasLoadedDestinations = false
        hasLoadedTrips = false
        loadDestinationsIfNeeded()
        loadTripsIfNeeded()
    }

    private fun loadSelectedTabIfNeeded() {
        when (_state.value.selectedTab) {
            SectionTab.Destinations -> loadDestinationsIfNeeded()
            SectionTab.Trips -> loadTripsIfNeeded()
        }
    }

    private fun loadDestinationsIfNeeded(force: Boolean = false) {
        if (!force && hasLoadedDestinations) return
        fetchDestinationPage(pageIndex = FIRST_PAGE, isInitialLoad = true)
    }

    private fun fetchDestinationPage(
        pageIndex: Int,
        isInitialLoad: Boolean
    ) {
        if (isInitialLoad) {
            _state.update {
                it.copy(
                    destinationsState = Loading,
                    destinationsPagination = it.destinationsPagination.copy(
                        isLoadingMore = false,
                        loadMoreError = null
                    )
                )
            }
        } else {
            val currentPagination = _state.value.destinationsPagination
            if (currentPagination.isLoadingMore) return
            _state.update {
                it.copy(
                    destinationsPagination = it.destinationsPagination.copy(
                        isLoadingMore = true,
                        loadMoreError = null
                    )
                )
            }
        }

        viewModelScope.launch {
            when (val result = requestDestinationPage(pageIndex, PAGE_SIZE)) {
                is Result.Success -> {
                    applyDestinationPage(page = result.data, replaceExisting = isInitialLoad)
                    hasLoadedDestinations = true
                    if (isInitialLoad) {
                        _effect.trySend(FavoriteEffect.ScrollToTop)
                    }
                }

                is Result.Error -> {
                    if (isInitialLoad) {
                        _state.update {
                            it.copy(
                                destinationsState = Error(UiText.StringResource(R.string.favorite_error_destinations)),
                                destinationsPagination = FavoritesPaginationState(pageSize = PAGE_SIZE)
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                destinationsPagination = it.destinationsPagination.copy(
                                    isLoadingMore = false,
                                    loadMoreError = UiText.StringResource(R.string.favorite_error_destinations)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun requestDestinationPage(
        pageIndex: Int,
        pageSize: Int
    ): Result<com.example.domain.model.favorite.FavoritesPage, DataError> {
        return getFavoriteDestinationsPageUseCase(pageIndex = pageIndex, pageSize = pageSize)
    }

    private fun applyDestinationPage(
        page: com.example.domain.model.favorite.FavoritesPage,
        replaceExisting: Boolean
    ) {
        val mappedPageItems = page.items
            .filter { it.destinationId > 0 }
            .map { destination ->
                com.example.domain.model.favorite.Place(
                    id = destination.destinationId,
                    name = destination.name.ifBlank {
                        destination.cityName.ifBlank { destination.description }
                    },
                    description = destination.cityName.ifBlank { destination.description },
                    imageUrls = destination.imageUrls
                )
            }

        val mergedItems = if (replaceExisting) {
            mappedPageItems
        } else {
            (_state.value.loadedDestinations + mappedPageItems).distinctBy { it.id }
        }

        val hasMore = mergedItems.size < page.count
        val updatedTabState = if (mergedItems.isEmpty()) Empty else Success(mergedItems)

        _state.update {
            it.copy(
                loadedDestinations = mergedItems,
                destinationsState = updatedTabState,
                destinationsPagination = it.destinationsPagination.copy(
                    currentPageIndex = page.pageIndex,
                    pageSize = page.pageSize,
                    totalCount = page.count,
                    loadedCount = mergedItems.size,
                    hasMore = hasMore,
                    isLoadingMore = false,
                    loadMoreError = null
                )
            )
        }
    }

    private fun loadTripsIfNeeded(force: Boolean = false) {
        if (!force && hasLoadedTrips) return
        fetchTripsPage(pageIndex = FIRST_PAGE, isInitialLoad = true)
    }

    private fun fetchTripsPage(
        pageIndex: Int,
        isInitialLoad: Boolean
    ) {
        if (isInitialLoad) {
            _state.update {
                it.copy(
                    tripsState = Loading,
                    tripsPagination = it.tripsPagination.copy(
                        isLoadingMore = false,
                        loadMoreError = null
                    )
                )
            }
        } else {
            val currentPagination = _state.value.tripsPagination
            if (currentPagination.isLoadingMore) return
            _state.update {
                it.copy(
                    tripsPagination = it.tripsPagination.copy(
                        isLoadingMore = true,
                        loadMoreError = null
                    )
                )
            }
        }

        viewModelScope.launch {
            val result = requestTripsPage(pageIndex, PAGE_SIZE)
            result.onSuccess { data ->
                applyTripsPage(page = data, replaceExisting = isInitialLoad)
                hasLoadedTrips = true
                if (isInitialLoad) {
                    _effect.trySend(FavoriteEffect.ScrollToTop)
                }
            }.onFailure { e ->
                if (isInitialLoad) {
                    _state.update {
                        it.copy(
                            tripsState = Error(UiText.StringResource(R.string.favorite_error_trips)),
                            tripsPagination = FavoritesPaginationState(pageSize = PAGE_SIZE)
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            tripsPagination = it.tripsPagination.copy(
                                isLoadingMore = false,
                                loadMoreError = UiText.StringResource(R.string.favorite_error_trips)
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun requestTripsPage(
        pageIndex: Int,
        pageSize: Int
    ): kotlin.Result<FavoriteTripsPage> {
        return getFavoriteTripsUseCase(pageIndex = pageIndex, pageSize = pageSize)
    }

    private fun applyTripsPage(
        page: FavoriteTripsPage,
        replaceExisting: Boolean
    ) {
        val mappedPageItems = page.data

        val mergedItems = if (replaceExisting) {
            mappedPageItems
        } else {
            (_state.value.loadedTrips + mappedPageItems).distinctBy { it.id }
        }

        val hasMore = mergedItems.size < page.count
        val updatedTabState = if (mergedItems.isEmpty()) Empty else Success(mergedItems)

        _state.update {
            it.copy(
                loadedTrips = mergedItems,
                tripsState = updatedTabState,
                tripsPagination = it.tripsPagination.copy(
                    currentPageIndex = page.pageIndex,
                    pageSize = page.pageSize,
                    totalCount = page.count,
                    loadedCount = mergedItems.size,
                    hasMore = hasMore,
                    isLoadingMore = false,
                    loadMoreError = null
                )
            )
        }
    }

    fun onTripItemVisible(index: Int) {
        if (_state.value.selectedTab != SectionTab.Trips) return
        val remainingItems = _state.value.loadedTrips.lastIndex - index
        if (remainingItems <= PREFETCH_THRESHOLD) {
            onLoadMoreCurrentTab()
        }
    }

    fun onTabSelected(tab: SectionTab) {
        _state.update { it.copy(selectedTab = tab) }
        viewModelScope.launch {
            saveFavoriteSelectedTabUseCase(tab.name)
        }
        loadSelectedTabIfNeeded()
    }

    fun onDestinationItemVisible(index: Int) {
        if (_state.value.selectedTab != SectionTab.Destinations) return
        val remainingItems = _state.value.loadedDestinations.lastIndex - index
        if (remainingItems <= PREFETCH_THRESHOLD) {
            onLoadMoreCurrentTab()
        }
    }

    fun onRetryCurrentTab() {
        when (_state.value.selectedTab) {
            SectionTab.Destinations -> loadDestinationsIfNeeded(force = true)
            SectionTab.Trips -> loadTripsIfNeeded(force = true)
        }
    }

    fun onLoadMoreCurrentTab() {
        when (_state.value.selectedTab) {
            SectionTab.Destinations -> loadMoreDestinations()
            SectionTab.Trips -> loadMoreTrips()
        }
    }

    fun onRetryLoadMoreCurrentTab() {
        when (_state.value.selectedTab) {
            SectionTab.Destinations -> loadMoreDestinations(force = true)
            SectionTab.Trips -> loadMoreTrips(force = true)
        }
    }

    fun onDestinationFavoriteToggled(
        destinationId: Int,
        shouldFavorite: Boolean
    ) {
        if (destinationId <= 0) {
            _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_invalid_destination_id)))
            return
        }
        val intent = if (shouldFavorite) MutationIntent.Add else MutationIntent.Remove
        processDestinationMutation(destinationId = destinationId, intent = intent)
    }

    private fun processDestinationMutation(
        destinationId: Int,
        intent: MutationIntent
    ) {
        if (destinationId in _state.value.inFlightMutationIds) {
            queuedMutationIntents[destinationId] = intent
            return
        }

        activeMutationIntents[destinationId] = intent
        val beforeItems = _state.value.loadedDestinations

        val optimisticItems = when (intent) {
            MutationIntent.Add -> {
                beforeItems
            }

            MutationIntent.Remove -> beforeItems.filterNot { it.id == destinationId }
        }

        _state.update {
            it.copy(
                inFlightMutationIds = it.inFlightMutationIds + destinationId,
                loadedDestinations = optimisticItems,
                destinationsState = if (optimisticItems.isEmpty()) Empty else Success(optimisticItems)
            )
        }

        viewModelScope.launch {
            val result = when (intent) {
                MutationIntent.Add -> addDestinationFavoriteUseCase(destinationId)
                MutationIntent.Remove -> removeDestinationFavoriteUseCase(destinationId)
            }

            if (result is Result.Error) {
                _state.update {
                    it.copy(
                        loadedDestinations = beforeItems,
                        destinationsState = if (beforeItems.isEmpty()) Empty else Success(beforeItems)
                    )
                }
                _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_error_destinations)))
            } else {
                reconcileFavoritesAfterMutation()
            }

            _state.update {
                it.copy(inFlightMutationIds = it.inFlightMutationIds - destinationId)
            }

            val activeIntent = activeMutationIntents.remove(destinationId)
            val queuedIntent = queuedMutationIntents.remove(destinationId)
            if (queuedIntent != null && queuedIntent != activeIntent) {
                processDestinationMutation(destinationId = destinationId, intent = queuedIntent)
            }
        }
    }

    private fun reconcileFavoritesAfterMutation() {
        // Re-fetch first page so favorites list and shared IDs settle to server truth after mutation.
        fetchDestinationPage(pageIndex = FIRST_PAGE, isInitialLoad = true)
    }

    private fun loadMoreDestinations(force: Boolean = false) {
        val pagination = _state.value.destinationsPagination
        if (!force && (!pagination.hasMore || pagination.isLoadingMore)) return

        val nextPageIndex = if (pagination.currentPageIndex < FIRST_PAGE) FIRST_PAGE else pagination.currentPageIndex + 1
        fetchDestinationPage(pageIndex = nextPageIndex, isInitialLoad = false)
    }

    private fun loadMoreTrips(force: Boolean = false) {
        val pagination = _state.value.tripsPagination
        if (!force && (!pagination.hasMore || pagination.isLoadingMore)) return

        val nextPageIndex = if (pagination.currentPageIndex < FIRST_PAGE) FIRST_PAGE else pagination.currentPageIndex + 1
        fetchTripsPage(pageIndex = nextPageIndex, isInitialLoad = false)
    }

    fun onDeletePlace(placeId: String) {
        val destinationId = placeId.toIntOrNull() ?: return
        onDestinationFavoriteToggled(destinationId = destinationId, shouldFavorite = false)
    }

    fun onFavoriteTripToggled(
        tripId: Int,
        shouldFavorite: Boolean
    ) {
        if (tripId <= 0) {
            _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.error_invalid_trip_id)))
            return
        }
        val intent = if (shouldFavorite) MutationIntent.Add else MutationIntent.Remove
        processTripMutation(tripId = tripId, intent = intent)
    }

    private fun processTripMutation(
        tripId: Int,
        intent: MutationIntent
    ) {
        if (tripId in _state.value.inFlightMutationIds) {
            queuedMutationIntents[tripId] = intent
            return
        }

        activeMutationIntents[tripId] = intent
        val beforeItems = _state.value.loadedTrips

        val optimisticItems = when (intent) {
            MutationIntent.Add -> {
                beforeItems.map { if (it.id == tripId) it.copy(isFavorite = true) else it }
            }
            MutationIntent.Remove -> {
                beforeItems.filterNot { it.id == tripId }
            }
        }

        _state.update {
            it.copy(
                inFlightMutationIds = it.inFlightMutationIds + tripId,
                loadedTrips = optimisticItems,
                tripsState = if (optimisticItems.isEmpty()) Empty else Success(optimisticItems)
            )
        }

        viewModelScope.launch {
            val isFavorite = intent == MutationIntent.Add
            val result = toggleFavoriteTripUseCase(tripId, isFavorite)

            if (result.isFailure) {
                _state.update {
                    it.copy(
                        loadedTrips = beforeItems,
                        tripsState = if (beforeItems.isEmpty()) Empty else Success(beforeItems)
                    )
                }
                _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_error_trips)))
            } else {
                reconcileTripsAfterMutation()
            }

            _state.update {
                it.copy(inFlightMutationIds = it.inFlightMutationIds - tripId)
            }

            val activeIntent = activeMutationIntents.remove(tripId)
            val queuedIntent = queuedMutationIntents.remove(tripId)
            if (queuedIntent != null && queuedIntent != activeIntent) {
                processTripMutation(tripId = tripId, intent = queuedIntent)
            }
        }
    }

    private fun reconcileTripsAfterMutation() {
        fetchTripsPage(pageIndex = FIRST_PAGE, isInitialLoad = true)
    }

    fun onHomeClicked() {
        _state.update { it.copy(selectedItem = 0) }
    }

    fun onFavoriteClicked() {
        _state.update { it.copy(selectedItem = 1) }
    }

    fun onCommunityClicked() {
        _state.update { it.copy(selectedItem = 2) }
    }

    fun onAiChatClicked() {
        _state.update { it.copy(selectedItem = 3) }
    }

    fun onProfileClicked() {
        _state.update { it.copy(selectedItem = 4) }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 10
        const val PREFETCH_THRESHOLD = 3
    }

    private enum class MutationIntent {
        Add,
        Remove
    }
}
