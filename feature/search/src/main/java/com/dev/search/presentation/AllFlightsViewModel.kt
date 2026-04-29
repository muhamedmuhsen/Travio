package com.dev.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.asUiText
import com.example.domain.usecase.flights.GetTopFlightOffersUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllFlightsViewModel @Inject constructor(
    private val getTopFlightOffersUseCase: GetTopFlightOffersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TopOffersUiState>(TopOffersUiState.Idle)
    val uiState: StateFlow<TopOffersUiState> = _uiState.asStateFlow()

    // keep domain models for detail navigation
    private var lastTopFlightOffers: List<com.example.domain.model.flights.TopFlightOffer> = emptyList()

    init {
        loadOffers()
    }

    fun loadOffers(forceRefresh: Boolean = false) {
        // Prevent duplicate loads
        if (_uiState.value is TopOffersUiState.Loading) return

        _uiState.value = TopOffersUiState.Loading
        viewModelScope.launch {
            when (val result = getTopFlightOffersUseCase(forceRefresh = forceRefresh, limit = null)) {
                is Result.Error -> {
                    val msg = result.error.asUiText()
                    _uiState.value = TopOffersUiState.Error(msg)
                    Timber.w("AllFlightsViewModel: failed to load offers -> ${result.error}")
                }
                is Result.Success -> {
                    lastTopFlightOffers = result.data
                    _uiState.value = TopOffersUiState.Success(result.data)
                }
            }
        }
    }

    fun onRetry() {
        loadOffers(forceRefresh = true)
    }

    fun onRefresh() {
        loadOffers(forceRefresh = true)
    }

    /** Find a previously-loaded TopFlightOffer by its offerId without performing a network call. */
    fun findTopOfferById(offerId: String): com.example.domain.model.flights.TopFlightOffer? {
        return lastTopFlightOffers.firstOrNull { it.offerId == offerId }
    }
}
