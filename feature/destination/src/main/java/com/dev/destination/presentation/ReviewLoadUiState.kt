package com.dev.destination.presentation

import com.dev.utils.uitext.UiText
import com.example.domain.model.review.Review

sealed interface ReviewLoadUiState {
    data object Idle : ReviewLoadUiState
    data class Loading(
        val requestKey: String,
        val retryCount: Int
    ) : ReviewLoadUiState

    data class Success(
        val requestKey: String,
        val reviews: List<Review>
    ) : ReviewLoadUiState

    data class Error(
        val requestKey: String,
        val message: UiText,
        val retryCount: Int
    ) : ReviewLoadUiState
}
