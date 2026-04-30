package com.dev.search.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.dev.utils.uitext.asUiText
import com.example.common.extensions.toFlightDuration
import com.example.common.navigation.Screen
import com.example.designsystem.theme.TravioTheme
import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FlightDetailUiState {
    object Loading : FlightDetailUiState
    data class Success(val offer: TopFlightOffer) : FlightDetailUiState
    data class Error(val message: UiText) : FlightDetailUiState
}

@HiltViewModel
class FlightDetailViewModel @Inject constructor(
    private val getTopFlightOffersUseCase: GetTopFlightOffersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlightDetailUiState>(FlightDetailUiState.Loading)
    val uiState: StateFlow<FlightDetailUiState> = _uiState.asStateFlow()

    init {
        val offerId = savedStateHandle.get<String>(Screen.FlightDetailScreen.ARG_OFFER_ID) ?: ""
        viewModelScope.launch {
            // Use the use case with forceRefresh = false so repository/cache returns in-memory data when available
            when (val result = getTopFlightOffersUseCase(forceRefresh = false)) {
                is Result.Success -> {
                    val found = result.data.firstOrNull { it.offerId == offerId }
                    if (found != null) {
                        _uiState.value = FlightDetailUiState.Success(found)
                    } else {
                        _uiState.value = FlightDetailUiState.Error(UiText.DynamicString("Offer not found"))
                    }
                }

                is Result.Error -> {
                    _uiState.value = FlightDetailUiState.Error(result.error.asUiText())
                }
            }
        }
    }
}

@Composable
fun FlightDetailScreen(
    offerId: String,
    onBack: () -> Unit,
    viewModel: FlightDetailViewModel = hiltViewModel()
) {
    // The ViewModel obtains the offerId from SavedStateHandle; the parameter is preserved for clarity.
    val state by viewModel.uiState.collectAsState()

    TravioTheme {
        when (state) {
            is FlightDetailUiState.Loading -> {
                // Minimal placeholder (we purposely avoid heavy UI here)
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            }

            is FlightDetailUiState.Error -> {
                val msg = (state as FlightDetailUiState.Error).message
                Text(text = msg.asString(), style = MaterialTheme.typography.bodyLarge)
            }

            is FlightDetailUiState.Success -> {
                val offer = (state as FlightDetailUiState.Success).offer
                Column {
                    Text(text = offer.destinationCityName, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "From: ${offer.origin} → To: ${offer.destination}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Duration: ${offer.duration.toFlightDuration()}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Airline: ${offer.airlineName}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Price: ${offer.cheapestPrice} ${offer.currency}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
