package com.example.feature.chat.presentation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.UiText
import com.example.domain.model.trip.TripSyncEvent
import com.example.domain.usecase.trip.DeleteServerTripUseCase
import com.example.domain.usecase.trip.GetTripDetailsUseCase
import com.example.domain.usecase.trip.ObserveTripSyncEventsUseCase
import com.example.domain.usecase.trip.ToggleFavoriteTripUseCase
import com.example.feature.chat.domain.repository.TripRepository
import com.example.feature.chat.presentation.state.TripDetailUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TripDetailUiState(
    val days: List<DayItinerary> = emptyList(),
    val selectedDayId: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null,
    val isDeleteDialogVisible: Boolean = false,
    val tripId: String? = null,
    val isFavorite: Boolean = false,
    val isFavoriteToggling: Boolean = false
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
    private val tripRepository: TripRepository,
    private val getTripDetailsUseCase: GetTripDetailsUseCase,
    private val deleteServerTripUseCase: DeleteServerTripUseCase,
    private val toggleFavoriteTripUseCase: ToggleFavoriteTripUseCase,
    private val observeTripSyncEventsUseCase: ObserveTripSyncEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = kotlinx.coroutines.channels.Channel<TripDetailUiEvent>(kotlinx.coroutines.channels.Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeTripSyncEvents()
    }

    private fun observeTripSyncEvents() {
        viewModelScope.launch {
            observeTripSyncEventsUseCase().collect { event ->
                val currentTripId = _uiState.value.tripId?.toIntOrNull() ?: return@collect
                if (event is TripSyncEvent.FavoriteToggled && event.tripId == currentTripId) {
                    _uiState.update { it.copy(isFavorite = event.isFavorite) }
                }
            }
        }
    }

    fun loadTrip(tripId: String) {
        _uiState.update { it.copy(isLoading = true, error = null, tripId = tripId) }
        viewModelScope.launch {
            val numericId = tripId.toIntOrNull()
            if (numericId != null) {
                val result = getTripDetailsUseCase(numericId)
                result.onSuccess { trip ->
                    val days = trip.days.map { day ->
                        DayItinerary(
                            id = day.dayNumber.toString(),
                            title = "Day ${day.dayNumber}",
                            subtitle = day.theme,
                            recommendedHotels = trip.hotels.mapIndexed { index, hotel ->
                                RecommendedHotel(
                                    id = index.toString(),
                                    name = hotel.name,
                                    location = hotel.address,
                                    price = "",
                                    rating = hotel.rating.toFloat(),
                                    imageUrl = hotel.featuredImage.ifEmpty { null }
                                )
                            },
                            activities = day.activities.mapIndexed { index, act ->
                                ActivityItem(
                                    id = "${day.dayNumber}_$index",
                                    time = act.suggestedTime,
                                    title = act.placeName,
                                    description = act.description,
                                    price = "",
                                    tag = act.activityType,
                                    imageUrl = act.featuredImage.ifEmpty { null }
                                )
                            }
                        )
                    }

                    _uiState.update {
                        it.copy(
                            days = days,
                            selectedDayId = days.firstOrNull()?.id.orEmpty(),
                            isLoading = false,
                            isFavorite = trip.isFavorite
                        )
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
            } else {
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
    }

    fun selectDay(dayId: String) {
        _uiState.update { it.copy(selectedDayId = dayId) }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun deleteTrip() {
        val tripId = _uiState.value.tripId ?: return
        val numericId = tripId.toIntOrNull()

        _uiState.update { it.copy(isDeleting = true, deleteError = null) }
        viewModelScope.launch {
            if (numericId != null) {
                val result = deleteServerTripUseCase(numericId)
                result.onSuccess {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true, isDeleteDialogVisible = false) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isDeleting = false, deleteError = e.message ?: "Failed to delete trip") }
                }
            } else {
                try {
                    tripRepository.deleteTripPlan(tripId)
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true, isDeleteDialogVisible = false) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isDeleting = false, deleteError = e.message ?: "Failed to delete local trip") }
                }
            }
        }
    }

    fun toggleFavorite() {
        if (_uiState.value.isFavoriteToggling) return
        val tripId = _uiState.value.tripId ?: return
        val numericId = tripId.toIntOrNull()
        if (numericId == null) {
            viewModelScope.launch {
                _uiEvent.send(
                    TripDetailUiEvent.ShowSnackbar(UiText.StringResource(com.example.feature.chat.R.string.error_cannot_favorite_local))
                )
            }
            return
        }

        val currentFavoriteState = _uiState.value.isFavorite

        // Optimistic update
        _uiState.update { it.copy(isFavorite = !currentFavoriteState, isFavoriteToggling = true) }

        viewModelScope.launch {
            val result = toggleFavoriteTripUseCase(numericId, !currentFavoriteState)
            result.onFailure { e ->
                // Revert on failure
                _uiState.update {
                    it.copy(
                        isFavorite = currentFavoriteState,
                        isFavoriteToggling = false
                    )
                }
                viewModelScope.launch {
                    val errorMsg = e.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(com.example.feature.chat.R.string.error_failed_toggle_favorite)
                    _uiEvent.send(TripDetailUiEvent.ShowSnackbar(errorMsg))
                }
            }
            result.onSuccess {
                _uiState.update { it.copy(isFavoriteToggling = false) }
            }
        }
    }
}
