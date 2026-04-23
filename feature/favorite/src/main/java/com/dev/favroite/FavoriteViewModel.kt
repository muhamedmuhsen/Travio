package com.dev.favroite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.favroite.FavoritesTabUiState.Empty
import com.dev.favroite.FavoritesTabUiState.Error
import com.dev.favroite.FavoritesTabUiState.Loading
import com.dev.favroite.FavoritesTabUiState.Success
import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.domain.model.favorite.FavoriteDestination
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.GetFavoriteDestinationsPageUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.trip.DeleteTripUseCase
import com.example.domain.usecase.favorite.trip.GetAllTripsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.favorite.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val addDestinationFavoriteUseCase: AddDestinationFavoriteUseCase? = null,
    private val removeDestinationFavoriteUseCase: RemoveDestinationFavoriteUseCase? = null,
    private val observeFavoriteDestinationIdsUseCase: ObserveFavoriteDestinationIdsUseCase? = null,
    private val getFavoriteSelectedTabUseCase: GetFavoriteSelectedTabUseCase,
    private val saveFavoriteSelectedTabUseCase: SaveFavoriteSelectedTabUseCase,
    private val getFavoriteDestinationsPageUseCase: GetFavoriteDestinationsPageUseCase? = null
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
        warmSharedFavoriteSync()
        observeSharedFavoriteIds()
        viewModelScope.launch {
            val restoredTab = mapToSectionTab(getFavoriteSelectedTabUseCase())
            _state.update { it.copy(selectedTab = restoredTab) }
            loadSelectedTabIfNeeded()
        }
    }

    private fun observeSharedFavoriteIds() {
        val sharedObserver = observeFavoriteDestinationIdsUseCase ?: return
        viewModelScope.launch {
            sharedObserver()
                .catch { throwable ->
                    System.err.println("FavoriteViewModel observer failed: ${throwable.message}")
                    _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_sync_unavailable)))
                }
                .collect { ids ->
                    _state.update { it.copy(favoriteIds = ids) }
                }
        }
    }

    private fun warmSharedFavoriteSync() {
        val pageUseCase = getFavoriteDestinationsPageUseCase ?: return
        viewModelScope.launch {
            pageUseCase(pageIndex = FIRST_PAGE, pageSize = PAGE_SIZE)
        }
    }

    private fun mapToSectionTab(value: String?): SectionTab {
        return when (value) {
            SectionTab.Trips.name -> SectionTab.Trips
            else -> SectionTab.Destinations
        }
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
    ): Result<FavoritesPage, DataError> {
        val modernUseCase = getFavoriteDestinationsPageUseCase
        if (modernUseCase != null) {
            return modernUseCase(pageIndex = pageIndex, pageSize = pageSize)
        }

        if (pageIndex > FIRST_PAGE) {
            val currentItems = _state.value.loadedDestinations
            return Result.Success(
                FavoritesPage(
                    pageIndex = pageIndex,
                    pageSize = pageSize,
                    count = currentItems.size,
                    items = emptyList()
                )
            )
        }

        return try {
            val places = getAllPlacesUseCase().first()
            val mappedItems = places.map {
                FavoriteDestination(
                    destinationId = it.id,
                    name = it.name,
                    description = it.description,
                    rating = 0.0,
                    cityName = it.description,
                    imageUrls = it.imageUrls
                )
            }
            Result.Success(
                FavoritesPage(
                    pageIndex = FIRST_PAGE,
                    pageSize = pageSize,
                    count = mappedItems.size,
                    items = mappedItems
                )
            )
        } catch (_: Exception) {
            Result.Error(DataError.Network.UnexpectedResponse)
        }
    }

    private fun applyDestinationPage(
        page: FavoritesPage,
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
        _state.update { it.copy(tripsState = Loading) }
        viewModelScope.launch {
            getAllTripsUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            tripsState = Error(
                                UiText.DynamicString(
                                    e.message ?: "Unknown error"
                                )
                            )
                        )
                    }
                }
                .collect { trips ->
                    val validTrips = trips
                        .filter { it.title.isNotBlank() }
                        .sortedWith(compareByDescending<com.example.domain.model.favorite.Trip> { it.savedAt }.thenByDescending { it.id })
                    hasLoadedTrips = true
                    _state.update {
                        it.copy(
                            loadedTrips = validTrips,
                            tripsState = if (validTrips.isEmpty()) Empty else Success(validTrips),
                            tripsPagination = it.tripsPagination.copy(
                                currentPageIndex = FIRST_PAGE,
                                pageSize = validTrips.size.coerceAtLeast(1),
                                totalCount = validTrips.size,
                                loadedCount = validTrips.size,
                                hasMore = false,
                                isLoadingMore = false,
                                loadMoreError = null
                            )
                        )
                    }
                }
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
                MutationIntent.Add -> {
                    val addUseCase = addDestinationFavoriteUseCase
                    if (addUseCase != null) {
                        addUseCase(destinationId)
                    } else {
                        Result.Success(
                            com.example.domain.model.favorite.FavoriteMutationResult(
                                isSuccess = true,
                                message = null,
                                errors = emptyList()
                            )
                        )
                    }
                }

                MutationIntent.Remove -> {
                    val removeUseCase = removeDestinationFavoriteUseCase
                    if (removeUseCase != null) {
                        removeUseCase(destinationId)
                    } else {
                        Result.Success(
                            com.example.domain.model.favorite.FavoriteMutationResult(
                                isSuccess = true,
                                message = null,
                                errors = emptyList()
                            )
                        )
                    }
                }
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

        _state.update {
            it.copy(
                tripsPagination = it.tripsPagination.copy(
                    isLoadingMore = true,
                    loadMoreError = null
                )
            )
        }

        viewModelScope.launch {
            try {
                val page = getAllTripsUseCase().first()
                val merged = (_state.value.loadedTrips + page)
                    .distinctBy { it.id }
                    .sortedWith(compareByDescending<com.example.domain.model.favorite.Trip> { it.savedAt }.thenByDescending { it.id })

                _state.update {
                    it.copy(
                        loadedTrips = merged,
                        tripsState = if (merged.isEmpty()) Empty else Success(merged),
                        tripsPagination = it.tripsPagination.copy(
                            isLoadingMore = false,
                            loadMoreError = null,
                            hasMore = false
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        tripsPagination = it.tripsPagination.copy(
                            isLoadingMore = false,
                            loadMoreError = UiText.DynamicString(e.message ?: "Load more failed")
                        )
                    )
                }
            }
        }
    }

    fun onDeletePlace(placeId: String) {
        val destinationId = placeId.toIntOrNull() ?: return
        onDestinationFavoriteToggled(destinationId = destinationId, shouldFavorite = false)
    }

    fun onDeleteTrip(tripId: String) {
        viewModelScope.launch {
            val current = _state.value.loadedTrips
            val removedIndex = current.indexOfFirst { it.id.toString() == tripId }
            if (removedIndex == -1) return@launch
            val removedItem = current[removedIndex]
            val optimistic = current.filterNot { it.id.toString() == tripId }

            _state.update {
                it.copy(
                    loadedTrips = optimistic,
                    tripsState = if (optimistic.isEmpty()) Empty else Success(optimistic)
                )
            }

            when (deleteTripUseCase(tripId)) {
                is Result.Success -> Unit

                is Result.Error -> {
                    val rollback = _state.value.loadedTrips.toMutableList().apply {
                        add(removedIndex.coerceAtMost(size), removedItem)
                    }
                    _state.update {
                        it.copy(
                            loadedTrips = rollback,
                            tripsState = Success(rollback)
                        )
                    }
                    _effect.trySend(FavoriteEffect.ShowMessage(UiText.StringResource(R.string.favorite_error_trips)))
                }
            }
        }
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
