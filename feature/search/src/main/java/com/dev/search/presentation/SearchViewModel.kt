package com.dev.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uistate.UiState
import com.dev.utils.uitext.asUiText
import com.example.domain.model.destination.Destination
import com.example.domain.usecase.destinations.AddToRecentlyViewedUseCase
import com.example.domain.usecase.destinations.SearchForDestinationsUseCase
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
    private val searchForDestinationsUseCase: SearchForDestinationsUseCase,
    private val addToRecentlyViewedUseCase: AddToRecentlyViewedUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val saveRecentSearchUseCase: SaveRecentSearchUseCase,
    private val deleteRecentSearchUseCase: DeleteRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _event = Channel<SearchEvent>(Channel.UNLIMITED)
    val event = _event.receiveAsFlow()

    private val searchTrigger = MutableSharedFlow<SearchTrigger>(extraBufferCapacity = 1)

    init {
        observeRecentSearches()
        observeSearchTrigger()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChanged -> {
                _uiState.update { it.copy(query = action.query) }
                searchTrigger.tryEmit(SearchTrigger(action.query, isImmediate = false))
            }

            is SearchAction.OnDestinationClicked -> onDestinationClicked(action.destination)
            is SearchAction.OnRecentSearchClicked -> {
                _uiState.update { it.copy(query = action.query) }
                searchTrigger.tryEmit(SearchTrigger(action.query, isImmediate = true))
            }

            is SearchAction.OnDeleteRecentSearch -> viewModelScope.launch {
                deleteRecentSearchUseCase(action.query)
            }

            SearchAction.OnClearRecentSearches -> viewModelScope.launch {
                clearRecentSearchesUseCase()
            }

            SearchAction.OnBackClicked -> viewModelScope.launch { _event.send(SearchEvent.NavigateBack) }
            SearchAction.OnClearQuery -> {
                _uiState.update { it.copy(query = "", searchResultsState = UiState.Idle) }
                searchTrigger.tryEmit(SearchTrigger("", isImmediate = true))
            }

            SearchAction.OnRetrySearch -> {
                val query = _uiState.value.query
                if (query.isNotBlank()) {
                    searchTrigger.tryEmit(SearchTrigger(query, isImmediate = true))
                }
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
                    if (trigger.query.isBlank()) {
                        _uiState.update { it.copy(searchResultsState = UiState.Idle) }
                    } else {
                        performSearch(trigger.query)
                    }
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(searchResultsState = UiState.Loading) }
        when (
            val result = searchForDestinationsUseCase(
                keyword = query,
                pageIndex = 1,
                pageSize = 20
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
                saveRecentSearchUseCase(query)
            }
        }
    }

    private fun onDestinationClicked(destination: Destination) {
        viewModelScope.launch {
            addToRecentlyViewedUseCase(destination)
            _event.send(SearchEvent.NavigateToDestination(destination.destinationID.toString()))
        }
    }
}
