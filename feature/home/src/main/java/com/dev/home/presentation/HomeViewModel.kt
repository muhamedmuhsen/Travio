package com.dev.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetNearbyDestinationsUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.state.UiState
import ui.text.asUiText
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val getNearbyDestinationsUseCase: GetNearbyDestinationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _event = Channel<HomeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        loadHomeData()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnDestinationClicked -> navigateToDestination(action.id)
            HomeAction.OnSearchClicked -> navigateToSearch()
            is HomeAction.OnFavoriteClicked -> addToFavorite(action.id)
        }
    }

    private fun addToFavorite(id: String) {
        // TODO: save in local database
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
    }

    private fun loadRecommendedDestinations() {
        _uiState.update { state -> state.copy(recommendedDestinationsState = UiState.Loading) }

        viewModelScope.launch {
            when (
                val result = getAllDestinationsUseCase(
                    pageIndex = 1,
                    pageSize = 10,
                    cityId = 1,
                    interestId = 1
                )
            ) {
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(recommendedDestinationsState = UiState.Error(result.error.asUiText()))
                    }
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
