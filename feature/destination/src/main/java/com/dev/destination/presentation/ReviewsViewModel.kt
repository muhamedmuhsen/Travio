package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.asUiText
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewAggregate
import com.example.domain.model.review.ReviewSummary
import com.example.domain.usecase.review.DeleteReviewUseCase
import com.example.domain.usecase.review.GetReviewsPageUseCase
import com.example.domain.usecase.review.UpsertReviewUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val getReviewsPageUseCase: GetReviewsPageUseCase,
    private val upsertReviewUseCase: UpsertReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val destinationId: Int = checkNotNull(savedStateHandle["destinationId"]) {
        "Destination ID must be provided to ReviewsViewModel"
    }

    private val _state = MutableStateFlow(ReviewsState())
    val state: StateFlow<ReviewsState> = _state.asStateFlow()

    init {
        loadInitialReviews()
    }

    fun loadInitialReviews() {
        viewModelScope.launch {
            _state.update { it.copy(initialLoading = true, initialError = null) }
            when (val result = getReviewsPageUseCase(destinationId, 1, _state.value.pagination.pageSize)) {
                is Result.Success -> {
                    val page = result.data
                    val currentUserReview = page.reviews.find { it.isOwnedByCurrentUser }
                    _state.update {
                        it.copy(
                            initialLoading = false,
                            reviews = page.reviews,
                            currentUserReview = currentUserReview,
                            pagination = it.pagination.copy(
                                pageIndex = 1,
                                totalCount = page.totalCount,
                                hasMore = page.reviews.size < page.totalCount
                            ),
                            summary = calculateSummary(page.reviews, page.totalCount, aggregate = null),
                            isSummarySyncedFromServer = false
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            initialLoading = false,
                            initialError = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun loadMoreReviews() {
        val currentPagination = _state.value.pagination
        if (currentPagination.isLoadingMore || !currentPagination.hasMore) return

        viewModelScope.launch {
            _state.update { it.copy(pagination = it.pagination.copy(isLoadingMore = true, loadMoreError = null)) }
            val nextPageIndex = currentPagination.pageIndex + 1
            when (val result = getReviewsPageUseCase(destinationId, nextPageIndex, currentPagination.pageSize)) {
                is Result.Success -> {
                    val page = result.data
                    _state.update {
                        val newReviews = (it.reviews + page.reviews).distinctBy { review -> review.id }
                        it.copy(
                            reviews = newReviews,
                            pagination = it.pagination.copy(
                                pageIndex = nextPageIndex,
                                isLoadingMore = false,
                                totalCount = page.totalCount,
                                hasMore = newReviews.size < page.totalCount
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            pagination = it.pagination.copy(
                                isLoadingMore = false,
                                loadMoreError = result.error.asUiText()
                            )
                        )
                    }
                }
            }
        }
    }

    fun upsertReview(
        rating: Int,
        content: String
    ) {
        if (_state.value.isSubmitting) return

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = upsertReviewUseCase.invokeWithAggregate(destinationId, rating, content)) {
                is Result.Success -> {
                    val updatedReview = result.data.review!!
                    val aggregate = result.data.aggregate
                    _state.update {
                        val isNew = it.currentUserReview == null
                        val updatedList = if (isNew) {
                            listOf(updatedReview!!) + it.reviews
                        } else {
                            it.reviews.map { r -> if (r.id == updatedReview!!.id) updatedReview!! else r }
                        }
                        val fallbackTotalCount = if (isNew) it.pagination.totalCount + 1 else it.pagination.totalCount
                        val newTotalCount = aggregate?.totalReviews ?: fallbackTotalCount
                        val newSummary = calculateSummary(updatedList, newTotalCount, aggregate)
                        it.copy(
                            isSubmitting = false,
                            currentUserReview = updatedReview,
                            reviews = updatedList,
                            pagination = it.pagination.copy(totalCount = newTotalCount),
                            summary = newSummary,
                            isSummarySyncedFromServer = aggregate != null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isSubmitting = false, submitError = result.error.asUiText()) }
                }
            }
        }
    }

    fun deleteReview() {
        val currentUserReview = _state.value.currentUserReview ?: return
        if (_state.value.isDeleting) return

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, deleteError = null) }
            when (val result = deleteReviewUseCase(destinationId, currentUserReview.id)) {
                is Result.Success -> {
                    _state.update {
                        val updatedList = it.reviews.filter { r -> r.id != currentUserReview.id }
                        val newTotalCount = it.pagination.totalCount - 1
                        it.copy(
                            isDeleting = false,
                            currentUserReview = null,
                            reviews = updatedList,
                            pagination = it.pagination.copy(totalCount = newTotalCount),
                            summary = calculateSummary(updatedList, newTotalCount, aggregate = null),
                            isSummarySyncedFromServer = false
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isDeleting = false, deleteError = result.error.asUiText()) }
                }
            }
        }
    }

    private fun calculateSummary(
        reviews: List<Review>,
        totalCount: Int,
        aggregate: ReviewAggregate?
    ): ReviewSummary {
        if (aggregate != null) {
            return ReviewSummary(
                averageRating = aggregate.averageRating.toInt(),
                totalReviews = aggregate.totalReviews
            )
        }
        if (totalCount == 0) return ReviewSummary(0, 0)
        val avg = reviews.map { it.rating }.average().toInt()
        return ReviewSummary(avg, totalCount)
    }
}
