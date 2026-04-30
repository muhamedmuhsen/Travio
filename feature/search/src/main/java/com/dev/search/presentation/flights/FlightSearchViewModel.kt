package com.dev.search.presentation.flights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asUiText
import com.example.domain.usecase.flights.SearchFlightsUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FlightSearchViewModel @Inject constructor(
    private val searchFlightsUseCase: SearchFlightsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightSearchUiState())
    val uiState: StateFlow<FlightSearchUiState> = _uiState.asStateFlow()

    private val _events = Channel<FlightSearchEvent>()
    val events = _events.receiveAsFlow()

    private val searchTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            searchTrigger
                .debounce(300L)
                .collect {
                    if (validateInputs()) {
                        executeSearch()
                    }
                }
        }
    }

    fun onAction(action: FlightSearchAction) {
        when (action) {
            is FlightSearchAction.OnOriginChanged -> updateParam { it.copy(origin = action.origin) }
            is FlightSearchAction.OnDestinationChanged -> updateParam { it.copy(destination = action.destination) }
            is FlightSearchAction.OnDepartureDateChanged -> updateParam { it.copy(departureDate = action.date) }
            is FlightSearchAction.OnAdultsChanged -> updateParam { it.copy(adults = action.adults) }
            is FlightSearchAction.OnCabinClassChanged -> updateParam { it.copy(cabinClass = action.cabinClass) }
            is FlightSearchAction.OnMaxStopsChanged -> updateParam { it.copy(maxStops = action.maxStops) }

            FlightSearchAction.OnSearchClicked -> {
                searchTrigger.tryEmit(Unit)
            }
            FlightSearchAction.OnRetrySearch -> {
                searchTrigger.tryEmit(Unit)
            }
            FlightSearchAction.OnBackClicked -> {
                viewModelScope.launch { _events.send(FlightSearchEvent.NavigateBack) }
            }
        }
    }

    private fun updateParam(
        update:
        (com.example.domain.model.flights.search.FlightSearchParameters) -> com.example.domain.model.flights.search.FlightSearchParameters
    ) {
        _uiState.update { it.copy(searchParams = update(it.searchParams)) }
    }

    private fun executeSearch() {
        _uiState.update { it.copy(searchStatus = SearchStatus.Loading) }

        viewModelScope.launch {
            val params = _uiState.value.searchParams
            when (val result = searchFlightsUseCase(params)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(searchStatus = SearchStatus.Success(result.data))
                    }
                }
                is Result.Error -> {
                    val isLogical = result.error is com.example.domain.utils.DataError.Logical
                    val message = if (isLogical) {
                        val logicalError = result.error as com.example.domain.utils.DataError.Logical
                        logicalError.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringResource(com.example.feature.search.R.string.flight_search_error_route_unavailable)
                    } else {
                        result.error.asUiText()
                    }

                    _uiState.update {
                        it.copy(
                            searchStatus = SearchStatus.Error(
                                message = message,
                                // T021: distinguish error types
                                isRetryable = !isLogical
                            )
                        )
                    }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val params = _uiState.value.searchParams
        val errors = mutableMapOf<String, UiText>()

        if (params.origin.isBlank()) {
            errors["origin"] = UiText.StringResource(com.example.feature.search.R.string.flight_search_validation_origin_required)
        }
        if (params.destination.isBlank()) {
            errors["destination"] = UiText.StringResource(com.example.feature.search.R.string.flight_search_validation_destination_required)
        }
        if (params.origin.isNotBlank() && params.origin == params.destination) {
            errors["destination"] = UiText.StringResource(
                com.example.feature.search.R.string.flight_search_validation_origin_destination_same
            )
        }
        if (params.departureDate.isBlank()) {
            errors["departureDate"] = UiText.StringResource(com.example.feature.search.R.string.flight_search_validation_date_required)
        }
        if (params.adults < 1) {
            errors["adults"] = UiText.StringResource(com.example.feature.search.R.string.flight_search_validation_adults_required)
        }

        _uiState.update { it.copy(validationErrors = errors) }
        return errors.isEmpty()
    }
}
