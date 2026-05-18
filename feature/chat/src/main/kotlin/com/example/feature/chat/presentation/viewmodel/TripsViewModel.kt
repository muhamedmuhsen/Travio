package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.chat.domain.model.TripPlan
import com.example.feature.chat.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TripsUiState {
    data object Loading : TripsUiState
    data class Success(val trips: List<TripPlan>) : TripsUiState
    data class Error(val message: String) : TripsUiState
}

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val uiState: StateFlow<TripsUiState> = tripRepository.observeTrips()
        .map { trips ->
            TripsUiState.Success(trips)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TripsUiState.Loading
        )

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.deleteTripPlan(tripId)
        }
    }
}
