package com.example.feature.chat.presentation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.chat.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TripDetailUiState(
    val days: List<DayItinerary> = emptyList(),
    val selectedDayId: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@Immutable
data class DayItinerary(
    val id: String,
    val title: String,
    val subtitle: String,
    val recommendedHotels: List<RecommendedHotel>,
    val activities: List<ActivityItem>
)

@Immutable
data class RecommendedHotel(
    val id: String,
    val name: String,
    val location: String,
    val price: String,
    val rating: Float,
    val imageUrl: String?
)

@Immutable
data class ActivityItem(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    val price: String,
    val tag: String,
    val imageUrl: String?
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun loadTrip(tripId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val trip = tripRepository.getTripById(tripId)
                if (trip != null) {
                    val days = trip.dailyPlans.map { day ->
                        DayItinerary(
                            id = day.day.toString(),
                            title = "Day ${day.day}",
                            subtitle = day.theme,
                            recommendedHotels = trip.recommendedHotels.mapIndexed { index, hotel ->
                                RecommendedHotel(
                                    id = index.toString(),
                                    name = hotel.name,
                                    location = hotel.address ?: "Unknown",
                                    price = "",
                                    rating = hotel.rating?.toFloat() ?: 0f,
                                    imageUrl = hotel.imageUrl
                                )
                            },
                            activities = day.activities.mapIndexed { index, act ->
                                ActivityItem(
                                    id = "${day.day}_$index",
                                    time = act.suggestedTime ?: "",
                                    title = act.placeName,
                                    description = act.description ?: "",
                                    price = "",
                                    tag = act.type,
                                    imageUrl = act.imageUrl
                                )
                            }
                        )
                    }

                    _uiState.update {
                        it.copy(
                            days = days,
                            selectedDayId = days.firstOrNull()?.id.orEmpty(),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Trip not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun selectDay(dayId: String) {
        _uiState.update { it.copy(selectedDayId = dayId) }
    }
}
