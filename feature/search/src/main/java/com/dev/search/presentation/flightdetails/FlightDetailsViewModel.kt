package com.dev.search.presentation.flightdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asUiText
import com.example.common.extensions.toFlightDuration
import com.example.common.navigation.Screen
import com.example.domain.model.flights.details.FlightDetails
import com.example.domain.usecase.flights.GetFlightDetailsUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.search.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FlightDetailsAction {
    object OnRetry : FlightDetailsAction
    object OnBackClicked : FlightDetailsAction
}

sealed interface FlightDetailsEvent {
    object NavigateBack : FlightDetailsEvent
}

@HiltViewModel
class FlightDetailsViewModel @Inject constructor(
    private val getFlightDetailsUseCase: GetFlightDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val offerId = savedStateHandle.get<String>(Screen.FlightDetailScreen.ARG_OFFER_ID).orEmpty()

    private val _uiState = MutableStateFlow<FlightDetailsUiState>(FlightDetailsUiState.Loading)
    val uiState: StateFlow<FlightDetailsUiState> = _uiState.asStateFlow()

    private val _events = Channel<FlightDetailsEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadDetails(forceRefresh = false)
    }

    fun onAction(action: FlightDetailsAction) {
        when (action) {
            FlightDetailsAction.OnRetry -> loadDetails(forceRefresh = true)
            FlightDetailsAction.OnBackClicked -> viewModelScope.launch {
                _events.send(FlightDetailsEvent.NavigateBack)
            }
        }
    }

    private fun loadDetails(forceRefresh: Boolean) {
        if (offerId.isBlank()) {
            _uiState.value = FlightDetailsUiState.Error(
                message = UiText.StringResource(R.string.flight_details_offer_not_found),
                isRetryable = true
            )
            return
        }

        _uiState.update { FlightDetailsUiState.Loading }
        viewModelScope.launch {
            when (val result = getFlightDetailsUseCase(offerId = offerId, forceRefresh = forceRefresh)) {
                is Result.Success -> {
                    val details = result.data
                    val warning = if (!details.isTimeDataValid) {
                        UiText.StringResource(R.string.flight_details_time_warning)
                    } else {
                        null
                    }
                    _uiState.value = FlightDetailsUiState.Success(details.toUiModel(), warning)
                }

                is Result.Error -> {
                    _uiState.value = FlightDetailsUiState.Error(
                        message = result.error.asUiText(),
                        isRetryable = result.error !is DataError.Logical
                    )
                }
            }
        }
    }

    private fun FlightDetails.toUiModel(): FlightDetailsUiModel {
        val stopsLabel = when (stops) {
            0 -> UiText.StringResource(R.string.flight_details_non_stop)
            1 -> UiText.StringResource(R.string.flight_details_one_stop)
            else -> UiText.StringResource(R.string.flight_details_transit)
        }

        val summary = FlightDetailsSummaryUi(
            airlineName = segments.first().airlineName,
            flightNumber = segments.first().flightNumber,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            origin = originAirport,
            destination = destinationAirport,
            stopsLabel = stopsLabel,
            totalDuration = totalDuration.toFlightDuration()
        )

        val price = PriceSectionUi(
            basePrice = priceBreakdown.basePrice,
            taxes = priceBreakdown.taxes,
            totalPrice = priceBreakdown.totalPrice,
            currency = currency,
            pricePerPerson = priceBreakdown.pricePerPerson
        )

        val flightInfo = FlightInfoUi(
            flightNumber = segments.first().flightNumber,
            aircraftName = segments.first().aircraftName,
            cabinClass = null,
            stopsLabel = stopsLabel,
            airlineName = segments.first().airlineName
        )

        val timeline = buildList {
            segments.forEachIndexed { index, segment ->
                add(TimelineItemUi.Departure(segment.originAirport, segment.departureTime))
                add(TimelineItemUi.Flight("${segment.airlineName} ${segment.flightNumber}", segment.segmentDuration.toFlightDuration()))
                if (index < layovers.size) {
                    val layover = layovers[index]
                    add(TimelineItemUi.Layover(layover.duration.toFlightDuration(), layover.location))
                }
                if (index == segments.lastIndex) {
                    add(TimelineItemUi.Arrival(segment.destinationAirport, segment.arrivalTime))
                }
            }
        }

        val extras = ExtrasUi(
            checkedBags = policies.checkedBags,
            baggageNote = policies.baggageNote,
            refundable = policies.refundable,
            penaltyAmount = policies.penaltyAmount
        )

        return FlightDetailsUiModel(
            summary = summary,
            price = price,
            flightInfo = flightInfo,
            timeline = timeline,
            extras = extras
        )
    }
}
