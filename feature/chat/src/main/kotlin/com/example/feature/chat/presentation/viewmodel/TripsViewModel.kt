package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.trip.TripItem
import com.example.domain.repository.trip.TripApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TripsUiState {
    data object Loading : TripsUiState
    data class Success(val trips: List<TripItem>) : TripsUiState
    data class Error(val message: String) : TripsUiState
}

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUiState>(TripsUiState.Loading)
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = TripsUiState.Loading
            tripRepository.getTrips(pageIndex = 1, pageSize = 50)
                .onSuccess { page ->
                    _uiState.value = TripsUiState.Success(page.data)
                }
                .onFailure {
                    _uiState.value = TripsUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            val id = tripId.toIntOrNull() ?: return@launch
            tripRepository.deleteTrip(id).onSuccess {
                loadTrips()
            }
        }
    }
}
