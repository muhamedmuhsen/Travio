package com.dev.search.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.model.destination.Destination
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.SearchForDestinationsUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.search.ClearRecentSearchesUseCase
import com.example.domain.usecase.search.DeleteRecentSearchUseCase
import com.example.domain.usecase.search.GetRecentSearchesUseCase
import com.example.domain.usecase.search.SaveRecentSearchUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchForDestinationsUseCase: SearchForDestinationsUseCase,
    private val addToRecentlyViewedUseCase: AddToRecentlyViewedUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val saveRecentSearchUseCase: SaveRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
    private val observeFavoriteDestinationIdsUseCase: ObserveFavoriteDestinationIdsUseCase,
    private val addDestinationFavoriteUseCase: AddDestinationFavoriteUseCase,
    private val removeDestinationFavoriteUseCase: RemoveDestinationFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(
            query = savedStateHandle["query"] ?: "",
            selectedInterestIds = (savedStateHandle.get<List<Int>>("interests") ?: emptyList()).toSet()
        )
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _event = Channel<SearchEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val searchTrigger = MutableSharedFlow<SearchTrigger>(extraBufferCapacity = 1)

    init {
        observeRecentSearches()
        observeSearchTrigger()
        observeFavoriteIds()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChanged -> {
                _uiState.update { it.copy(query = action.query) }
                savedStateHandle["query"] = action.query
                searchTrigger.tryEmit(
                    SearchTrigger(
                        query = action.query,
                        isImmediate = false,
                        selectedInterestIds = _uiState.value.selectedInterestIds.toList()
                    )
                )
            }

            is SearchAction.OnDestinationClicked -> onDestinationClicked(action.destination)
            is SearchAction.OnRecentSearchClicked -> {
                _uiState.update { it.copy(query = action.query) }
                savedStateHandle["query"] = action.query
                searchTrigger.tryEmit(
                    SearchTrigger(
                        query = action.query,
                        isImmediate = true,
                        selectedInterestIds = _uiState.value.selectedInterestIds.toList()
                    )
                )
            }

            is SearchAction.OnDeleteRecentSearch -> viewModelScope.launch {
                deleteRecentSearchUseCase(action.query)
            }

            SearchAction.OnClearRecentSearches -> viewModelScope.launch {
                clearRecentSearchesUseCase()
            }

            SearchAction.OnBackClicked -> viewModelScope.launch { _event.send(SearchEvent.NavigateBack) }
            SearchAction.OnClearQuery -> {
                _uiState.update { it.copy(query = "") }
                savedStateHandle["query"] = ""
                val state = _uiState.value
                if (state.selectedInterestIds.isEmpty()) {
                    _uiState.update { it.copy(searchResultsState = UiState.Idle) }
                }
                searchTrigger.tryEmit(
                    SearchTrigger(
                        query = "",
                        isImmediate = true,
                        selectedInterestIds = state.selectedInterestIds.toList()
                    )
                )
            }

            SearchAction.OnRetrySearch -> {
                val state = _uiState.value
                if (state.query.isNotBlank() || state.selectedInterestIds.isNotEmpty()) {
                    searchTrigger.tryEmit(
                        SearchTrigger(
                            query = state.query,
                            isImmediate = true,
                            selectedInterestIds = state.selectedInterestIds.toList()
                        )
                    )
                }
            }

            is SearchAction.OnInterestToggled -> {
                _uiState.update { state ->
                    val currentInterests = state.selectedInterestIds
                    val newInterests = if (currentInterests.contains(action.interestId)) {
                        currentInterests - action.interestId
                    } else {
                        currentInterests + action.interestId
                    }
                    state.copy(selectedInterestIds = newInterests)
                }
                val state = _uiState.value
                savedStateHandle["interests"] = state.selectedInterestIds.toList()
                searchTrigger.tryEmit(
                    SearchTrigger(
                        query = state.query,
                        isImmediate = true,
                        selectedInterestIds = state.selectedInterestIds.toList()
                    )
                )
            }

            is SearchAction.OnFavoriteToggled -> {
                toggleFavorite(action.destinationId, action.shouldFavorite)
            }
        }
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            getRecentSearchesUseCase()
                .catch { e -> Timber.e(e, "observeRecentSearches failed") }
                .collect { searches ->
                    _uiState.update { it.copy(recentSearches = searches) }
                }
        }
    }

    private fun observeSearchTrigger() {
        viewModelScope.launch {
            searchTrigger
                .debounce { trigger -> if (trigger.isImmediate) 0L else 300L }
                .collectLatest { trigger ->
                    if (trigger.query.isBlank() && trigger.selectedInterestIds.isEmpty()) {
                        _uiState.update { it.copy(searchResultsState = UiState.Idle) }
                    } else {
                        performSearch(trigger.query, trigger.selectedInterestIds)
                    }
                }
        }
    }

    private suspend fun performSearch(
        query: String,
        interestIds: List<Int>
    ) {
        _uiState.update { it.copy(searchResultsState = UiState.Loading) }
        when (
            val result = searchForDestinationsUseCase(
                keyword = query.ifBlank { null },
                pageIndex = 1,
                pageSize = 20,
                interestIds = interestIds.ifEmpty { null }
            )
        ) {
            is Result.Error -> {
                Timber.e("performSearch(%s): %s", query, result.error)
                val msg = result.error.asUiText()
                _uiState.update { it.copy(searchResultsState = UiState.Error(msg)) }
                _event.send(SearchEvent.ShowErrorSnackbar(msg))
            }

            is Result.Success -> {
                _uiState.update { it.copy(searchResultsState = UiState.Success(result.data)) }
                if (query.isNotBlank()) {
                    saveRecentSearchUseCase(query)
                }
            }
        }
    }

    private fun onDestinationClicked(destination: Destination) {
        viewModelScope.launch {
            addToRecentlyViewedUseCase(destination)
            _event.send(SearchEvent.NavigateToDestination(destination.destinationID.toString()))
        }
    }

    private fun observeFavoriteIds() {
        viewModelScope.launch {
            observeFavoriteDestinationIdsUseCase()
                .catch { e -> Timber.e(e, "observeFavoriteIds failed") }
                .collect { ids ->
                    _uiState.update { it.copy(favoriteIds = ids) }
                }
        }
    }

    private fun toggleFavorite(
        destinationId: Int,
        shouldFavorite: Boolean
    ) {
        val isCurrentlyFavorite = destinationId in _uiState.value.favoriteIds
        if (shouldFavorite == isCurrentlyFavorite) return

        _uiState.update { state ->
            val newFavorites = if (shouldFavorite) {
                state.favoriteIds + destinationId
            } else {
                state.favoriteIds - destinationId
            }
            state.copy(
                favoriteIds = newFavorites,
                favoriteMutationInFlightIds = state.favoriteMutationInFlightIds + destinationId
            )
        }

        viewModelScope.launch {
            val result = if (shouldFavorite) {
                addDestinationFavoriteUseCase(destinationId)
            } else {
                removeDestinationFavoriteUseCase(destinationId)
            }

            _uiState.update { it.copy(favoriteMutationInFlightIds = it.favoriteMutationInFlightIds - destinationId) }

            if (result is Result.Error) {
                // Rollback
                _uiState.update { state ->
                    val rollbackFavorites = if (shouldFavorite) {
                        state.favoriteIds - destinationId
                    } else {
                        state.favoriteIds + destinationId
                    }
                    state.copy(favoriteIds = rollbackFavorites)
                }
                _event.send(SearchEvent.ShowErrorSnackbar(result.error.asUiText()))
            }
        }
    }
}
