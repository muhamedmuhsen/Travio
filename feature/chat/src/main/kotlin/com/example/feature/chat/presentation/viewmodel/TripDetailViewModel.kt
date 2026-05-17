package com.example.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TripDetailUiState(
    val days: List<DayItinerary> = emptyList(),
    val selectedDayId: String = "",
    val isLoading: Boolean = true
)

data class DayItinerary(
    val id: String,
    val title: String,
    val subtitle: String,
    val recommendedHotels: List<RecommendedHotel>,
    val activities: List<ActivityItem>
)

data class RecommendedHotel(
    val id: String,
    val name: String,
    val location: String,
    val price: String,
    val rating: Float,
    val imageRes: Int
)

data class ActivityItem(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    val price: String,
    val tag: String,
    val imageRes: Int
)

@HiltViewModel
class TripDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockDays = listOf(
            DayItinerary(
                id = "day_1",
                title = "Day 1",
                subtitle = "Imperial Icons",
                recommendedHotels = listOf(
                    RecommendedHotel(
                        id = "h1",
                        name = "Hapi V",
                        location = "Downtown",
                        price = "$120 / night",
                        rating = 4.5f,
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    ),
                    RecommendedHotel(
                        id = "h2",
                        name = "Retreats",
                        location = "Historic District",
                        price = "$150 / night",
                        rating = 4.8f,
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    )
                ),
                activities = listOf(
                    ActivityItem(
                        id = "a1",
                        time = "08:30 AM",
                        title = "Breakfast at Anna Cafe",
                        description = "Fuel up with artisan coffee and a flaky croissant on a rustic table.",
                        price = "$25.00",
                        tag = "Breakfast",
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    ),
                    ActivityItem(
                        id = "a2",
                        time = "10:30 AM",
                        title = "Brandenburg Gate",
                        description = "Iconic neoclassical monument standing tall against the morning sky.",
                        price = "Free",
                        tag = "Attraction",
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    ),
                    ActivityItem(
                        id = "a3",
                        time = "01:00 PM",
                        title = "Bazaar Visit",
                        description = "Explore the vibrant local markets and shop for souvenirs.",
                        price = "$40.00",
                        tag = "Shopping",
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    )
                )
            ),
            DayItinerary(
                id = "day_2",
                title = "Day 2",
                subtitle = "Urban Zenith",
                recommendedHotels = listOf(
                    RecommendedHotel(
                        id = "h3",
                        name = "City Lights Inn",
                        location = "Central Park",
                        price = "$100 / night",
                        rating = 4.2f,
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    )
                ),
                activities = listOf(
                    ActivityItem(
                        id = "a4",
                        time = "09:00 AM",
                        title = "Modern Art Museum",
                        description = "Discover the latest contemporary masterpieces.",
                        price = "$30.00",
                        tag = "Museum",
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    ),
                    ActivityItem(
                        id = "a5",
                        time = "12:30 PM",
                        title = "Lunch at Zenith Sky Deck",
                        description = "Enjoy a panoramic view while having a luxurious lunch.",
                        price = "$55.00",
                        tag = "Lunch",
                        imageRes = com.example.designsystem.R.drawable.ishan_seefromthesky
                    )
                )
            )
        )

        _uiState.update {
            it.copy(
                days = mockDays,
                selectedDayId = mockDays.firstOrNull()?.id.orEmpty(),
                isLoading = false
            )
        }
    }

    fun selectDay(dayId: String) {
        _uiState.update { it.copy(selectedDayId = dayId) }
    }
}
