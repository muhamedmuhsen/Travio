package com.dev.favroite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.favroite.FavoritesTabUiState.Empty
import com.dev.favroite.FavoritesTabUiState.Error
import com.dev.favroite.FavoritesTabUiState.Loading
import com.dev.favroite.FavoritesTabUiState.Success
import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText
import com.example.domain.usecase.favorite.place.DeletePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.usecase.favorite.preference.GetFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.preference.SaveFavoriteSelectedTabUseCase
import com.example.domain.usecase.favorite.trip.DeleteTripUseCase
import com.example.domain.usecase.favorite.trip.GetAllTripsUseCase
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
    private val getFavoriteSelectedTabUseCase: GetFavoriteSelectedTabUseCase,
    private val saveFavoriteSelectedTabUseCase: SaveFavoriteSelectedTabUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()

    private val _effect = Channel<FavoriteEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var hasLoadedDestinations = false
    private var hasLoadedTrips = false

    init {
        viewModelScope.launch {
            val restoredTab = mapToSectionTab(getFavoriteSelectedTabUseCase())
            _state.update { it.copy(selectedTab = restoredTab) }
            loadSelectedTabIfNeeded()
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
        _state.update { it.copy(destinationsState = Loading) }
        viewModelScope.launch {
            getAllPlacesUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            destinationsState = Error(
                                UiText.DynamicString(
                                    e.message ?: "Unknown error"
                                )
                            )
                        )
                    }
                }
                .collect { places ->
                    val sortedPlaces = places
                        .filter { it.name.isNotBlank() }
                        .sortedByDescending { it.id }
                    hasLoadedDestinations = true
                    _state.update {
                        it.copy(
                            loadedDestinations = sortedPlaces,
                            destinationsState = if (sortedPlaces.isEmpty()) Empty else Success(sortedPlaces),
                            destinationsPagination = it.destinationsPagination.copy(
                                nextCursor = null,
                                hasMore = false,
                                isLoadingMore = false,
                                loadMoreError = null
                            )
                        )
                    }
                }
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
                                nextCursor = null,
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

    private fun loadMoreDestinations(force: Boolean = false) {
        val pagination = _state.value.destinationsPagination
        if (!force && (!pagination.hasMore || pagination.isLoadingMore)) return

        _state.update {
            it.copy(
                destinationsPagination = it.destinationsPagination.copy(
                    isLoadingMore = true,
                    loadMoreError = null
                )
            )
        }

        viewModelScope.launch {
            try {
                // Repository currently returns full snapshot flow; append step remains deduplicated.
                val page = getAllPlacesUseCase().first()
                val merged = (_state.value.loadedDestinations + page)
                    .distinctBy { it.id }
                    .sortedByDescending { it.id }

                _state.update {
                    it.copy(
                        loadedDestinations = merged,
                        destinationsState = if (merged.isEmpty()) Empty else Success(merged),
                        destinationsPagination = it.destinationsPagination.copy(
                            isLoadingMore = false,
                            loadMoreError = null,
                            hasMore = false,
                            nextCursor = null
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        destinationsPagination = it.destinationsPagination.copy(
                            isLoadingMore = false,
                            loadMoreError = UiText.DynamicString(e.message ?: "Load more failed")
                        )
                    )
                }
            }
        }
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
                // Repository currently returns full snapshot flow; append step remains deduplicated.
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
                            hasMore = false,
                            nextCursor = null
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
        viewModelScope.launch {
            val current = _state.value.loadedDestinations
            val removedIndex = current.indexOfFirst { it.id.toString() == placeId }
            if (removedIndex == -1) return@launch
            val removedItem = current[removedIndex]
            val optimistic = current.filterNot { it.id.toString() == placeId }

            _state.update {
                it.copy(
                    loadedDestinations = optimistic,
                    destinationsState = if (optimistic.isEmpty()) Empty else Success(optimistic)
                )
            }

            when (deletePlaceUseCase(placeId)) {
                is com.example.domain.utils.Result.Success -> {
                }

                is com.example.domain.utils.Result.Error -> {
                    val rollback = _state.value.loadedDestinations.toMutableList().apply {
                        add(removedIndex.coerceAtMost(size), removedItem)
                    }
                    _state.update {
                        it.copy(
                            loadedDestinations = rollback,
                            destinationsState = Success(rollback)
                        )
                    }
                    _effect.trySend(FavoriteEffect.ShowMessage(UiText.DynamicString("Failed to remove destination")))
                }
            }
        }
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
                is com.example.domain.utils.Result.Success -> {
                }

                is com.example.domain.utils.Result.Error -> {
                    val rollback = _state.value.loadedTrips.toMutableList().apply {
                        add(removedIndex.coerceAtMost(size), removedItem)
                    }
                    _state.update {
                        it.copy(
                            loadedTrips = rollback,
                            tripsState = Success(rollback)
                        )
                    }
                    _effect.trySend(FavoriteEffect.ShowMessage(UiText.DynamicString("Failed to remove trip")))
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
}
