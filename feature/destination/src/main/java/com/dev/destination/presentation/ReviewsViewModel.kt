package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.utils.uitext.asUiText
import com.example.domain.model.review.Review
import com.example.domain.model.review.ReviewAggregate
import com.example.domain.model.review.ReviewSummary
import com.example.domain.repository.usermanagement.UserManagementRepository
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
import kotlin.math.roundToInt

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val getReviewsPageUseCase: GetReviewsPageUseCase,
    private val upsertReviewUseCase: UpsertReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val userManagementRepository: UserManagementRepository,
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
                        val foundOwned = page.reviews.find { it.isOwnedByCurrentUser }
                        it.copy(
                            reviews = newReviews,
                            currentUserReview = it.currentUserReview ?: foundOwned,
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

            // Fetch user profile if we don't have metadata yet
            val userProfileResult = if (_state.value.currentUserReview == null) {
                userManagementRepository.getUser()
            } else {
                null
            }
            val userProfile = (userProfileResult as? Result.Success)?.data

            when (val result = upsertReviewUseCase.invokeWithAggregate(destinationId, rating, content)) {
                is Result.Success -> {
                    val updatedReview = result.data.review!!
                    val aggregate = result.data.aggregate
                    _state.update {
                        val enrichedReview = updatedReview.copy(
                            authorName = it.currentUserReview?.authorName?.takeIf { n -> n.isNotBlank() }
                                ?: userProfile?.let { u -> "${u.firstName} ${u.lastName}".trim() }.takeIf { n -> !n.isNullOrBlank() }
                                ?: userProfile?.username
                                ?: updatedReview.authorName,
                            authorAvatarUrl = it.currentUserReview?.authorAvatarUrl
                                ?: userProfile?.profilePictureUrl
                                ?: updatedReview.authorAvatarUrl,
                            rating = updatedReview.rating.takeIf { r -> r > 0 } ?: rating,
                            content = updatedReview.content.takeIf { c -> c.isNotBlank() } ?: content
                        )
                        val isNew = it.currentUserReview == null
                        val updatedList = if (isNew) {
                            listOf(enrichedReview) + it.reviews
                        } else {
                            it.reviews.map { r -> if (r.id == enrichedReview.id) enrichedReview else r }
                        }
                        val fallbackTotalCount = if (isNew) it.pagination.totalCount + 1 else it.pagination.totalCount
                        val newTotalCount = aggregate?.totalReviews ?: fallbackTotalCount
                        val newSummary = calculateSummary(updatedList, newTotalCount, aggregate)
                        it.copy(
                            isSubmitting = false,
                            currentUserReview = enrichedReview,
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
                        val isFullyLoaded = updatedList.size >= newTotalCount
                        val newSummary = if (isFullyLoaded) {
                            calculateSummary(updatedList, newTotalCount, null)
                        } else {
                            it.summary?.copy(totalReviews = newTotalCount)
                        }
                        it.copy(
                            isDeleting = false,
                            currentUserReview = null,
                            reviews = updatedList,
                            pagination = it.pagination.copy(totalCount = newTotalCount),
                            summary = newSummary,
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
                averageRating = aggregate.averageRating.roundToInt(),
                totalReviews = aggregate.totalReviews
            )
        }
        if (totalCount == 0) return ReviewSummary(0, 0)
        val avg = reviews.map { it.rating }.average().roundToInt()
        return ReviewSummary(avg, totalCount)
    }
}
