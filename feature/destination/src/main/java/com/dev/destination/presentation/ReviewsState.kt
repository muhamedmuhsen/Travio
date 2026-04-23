package com.dev.destination.presentation

import com.dev.utils.uitext.UiText
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewSummary

data class ReviewsPaginationState(
    val pageIndex: Int = 1,
    val pageSize: Int = 10,
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreError: UiText? = null
)

data class ReviewsState(
    val initialLoading: Boolean = false,
    val initialError: UiText? = null,
    val reviews: List<Review> = emptyList(),
    val pagination: ReviewsPaginationState = ReviewsPaginationState(),
    val summary: ReviewSummary? = null,
    val isSummarySyncedFromServer: Boolean = false,
    val currentUserReview: Review? = null,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val submitError: UiText? = null,
    val deleteError: UiText? = null,
    val draftRating: Int = 0,
    val draftContent: String = ""
) {
    val totalCount: Int get() = pagination.totalCount
}
