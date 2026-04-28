package com.dev.destination.presentation

import com.example.domain.model.destination.Destination
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewSummary

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class DestinationDetailUiState(
    val detailState: UiState<Destination> = UiState.Idle,
    val relatedDestinationsState: UiState<List<Destination>> = UiState.Idle,
    val reviewsState: UiState<List<Review>> = UiState.Idle,
    val reviewSummary: ReviewSummary? = null,
    val currentUserReview: Review? = null,
    val isFavorite: Boolean = false,
    val isFavoriteMutationInFlight: Boolean = false,
    val reviewText: String = "",
    val reviewRating: Int = 0,
    val isSubmittingReview: Boolean = false
)
