package com.dev.destination.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.common.navigation.DestinationDetailRoute
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.toPlace
import com.example.domain.model.review.ReviewSummary
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.repository.usermanagement.UserManagementRepository
import com.example.domain.usecase.destinations.GetAllDestinationsUseCase
import com.example.domain.usecase.destinations.GetDestinationByIdUseCase
import com.example.domain.usecase.favorite.destination.AddDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.destination.ObserveFavoriteDestinationIdsUseCase
import com.example.domain.usecase.favorite.destination.RemoveDestinationFavoriteUseCase
import com.example.domain.usecase.favorite.place.FavoritePlaceUseCase
import com.example.domain.usecase.favorite.place.GetAllPlacesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DestinationDetailViewModel @Inject constructor(
    private val getDestinationByIdUseCase: GetDestinationByIdUseCase,
    private val getAllDestinationsUseCase: GetAllDestinationsUseCase,
    private val favoritePlaceUseCase: FavoritePlaceUseCase,
    private val addDestinationFavoriteUseCase: AddDestinationFavoriteUseCase? = null,
    private val removeDestinationFavoriteUseCase: RemoveDestinationFavoriteUseCase? = null,
    private val observeFavoriteDestinationIdsUseCase: ObserveFavoriteDestinationIdsUseCase? = null,
    private val getAllPlacesUseCase: GetAllPlacesUseCase,
    private val reviewRepository: ReviewRepository,
    private val userManagementRepository: UserManagementRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val RELATED_PAGE_SIZE = 12
    }

    private val destinationId: Int? =
        runCatching { savedStateHandle.toRoute<DestinationDetailRoute>().id }.getOrNull()
            ?: savedStateHandle.get<Int>("id")

    private val _uiState = MutableStateFlow(DestinationDetailUiState())
    val uiState: StateFlow<DestinationDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<DestinationDetailEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeFavoriteState()
        loadDestination()
        loadReviews()
    }

    private fun loadReviews() {
        val id = destinationId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(reviewsState = UiState.Loading) }
            when (val result = reviewRepository.getReviewsByDestinationId(id)) {
                is Result.Success -> _uiState.update {
                    val currentUserReview = result.data.reviews.find { it.isOwnedByCurrentUser }
                    val averageRating =
                        if (result.data.totalCount == 0) {
                            0
                        } else {
                            result.data.reviews.map { review -> review.rating }.average().roundToInt()
                        }
                    it.copy(
                        reviewsState = UiState.Success(result.data.reviews),
                        reviewSummary = ReviewSummary(
                            averageRating = averageRating,
                            totalReviews = result.data.totalCount
                        ),
                        currentUserReview = currentUserReview,
                        reviewText = currentUserReview?.content ?: "",
                        reviewRating = currentUserReview?.rating ?: 0
                    )
                }
                is Result.Error -> _uiState.update { it.copy(reviewsState = UiState.Error("Failed to load reviews")) }
            }
        }
    }

    private fun submitReview() {
        val id = destinationId ?: return
        val text = uiState.value.reviewText
        val rating = uiState.value.reviewRating

        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReview = true) }
            when (val result = reviewRepository.submitReviewWithAggregate(id, rating, text)) {
                is Result.Success -> {
                    val updatedReview = result.data.review!!
                    val aggregate = result.data.aggregate

                    // Fetch user profile if we don't have metadata yet
                    val userProfileResult = if (uiState.value.currentUserReview == null) {
                        userManagementRepository.getUser()
                    } else {
                        null
                    }
                    val userProfile = (userProfileResult as? Result.Success)?.data

                    _uiState.update { state ->
                        val enrichedReview = updatedReview.copy(
                            authorName = state.currentUserReview?.authorName?.takeIf { n -> n.isNotBlank() }
                                ?: userProfile?.let { u -> "${u.firstName} ${u.lastName}".trim() }.takeIf { n -> !n.isNullOrBlank() }
                                ?: userProfile?.username
                                ?: updatedReview.authorName,
                            authorAvatarUrl = state.currentUserReview?.authorAvatarUrl
                                ?: userProfile?.profilePictureUrl
                                ?: updatedReview.authorAvatarUrl,
                            rating = updatedReview.rating.takeIf { r -> r > 0 } ?: rating,
                            content = updatedReview.content.takeIf { c -> c.isNotBlank() } ?: text
                        )

                        val currentReviews = (state.reviewsState as? UiState.Success)?.data.orEmpty()
                        val existingUserReviewIndex = currentReviews.indexOfFirst { it.isOwnedByCurrentUser }
                        val mergedReviews = if (existingUserReviewIndex >= 0) {
                            currentReviews.toMutableList().apply {
                                set(existingUserReviewIndex, enrichedReview)
                            }
                        } else {
                            listOf(enrichedReview) + currentReviews
                        }
                        val resolvedSummary = aggregate?.let {
                            ReviewSummary(
                                averageRating = it.averageRating.roundToInt(),
                                totalReviews = it.totalReviews
                            )
                        } ?: ReviewSummary(
                            averageRating = mergedReviews.map { review -> review.rating }.average().roundToInt(),
                            totalReviews = mergedReviews.size
                        )

                        val updatedDetailState = when (val detail = state.detailState) {
                            is UiState.Success -> {
                                UiState.Success(
                                    detail.data.copy(
                                        rating = resolvedSummary.averageRating.toDouble(),
                                        totalReviews = resolvedSummary.totalReviews
                                    )
                                )
                            }

                            else -> state.detailState
                        }

                        state.copy(
                            isSubmittingReview = false,
                            currentUserReview = enrichedReview,
                            reviewText = "",
                            reviewRating = 0,
                            detailState = updatedDetailState,
                            reviewSummary = resolvedSummary,
                            reviewsState = UiState.Success(mergedReviews)
                        )
                    }
                    _events.send(DestinationDetailEvent.ShowSuccessSnackbar("Review submitted successfully"))
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isSubmittingReview = false) }
                    _events.send(DestinationDetailEvent.ShowErrorSnackbar("Failed to submit review"))
                }
            }
        }
    }

    private fun deleteReview() {
        val id = destinationId ?: return
        val currentReviews = (_uiState.value.reviewsState as? UiState.Success)?.data
        val currentUserReview = currentReviews?.find { it.isOwnedByCurrentUser } ?: return
        val reviewId = currentUserReview.id

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReview = true) }
            when (val result = reviewRepository.deleteReviewWithAggregate(id, reviewId)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        val updatedReviews = state.reviewsState.let { rs ->
                            when (rs) {
                                is UiState.Success -> UiState.Success(rs.data.filter { it.id != reviewId })
                                else -> rs
                            }
                        }
                        state.copy(
                            isSubmittingReview = false,
                            currentUserReview = null,
                            reviewText = "",
                            reviewRating = 0,
                            reviewsState = updatedReviews,
                            reviewSummary = state.reviewSummary?.copy(
                                totalReviews = (state.reviewSummary?.totalReviews ?: 1) - 1
                            )
                        )
                    }
                    _events.send(DestinationDetailEvent.ShowSuccessSnackbar("Review deleted"))
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isSubmittingReview = false) }
                    _events.send(DestinationDetailEvent.ShowErrorSnackbar("Failed to delete review"))
                }
            }
        }
    }

    private fun retryReviews() {
        loadReviews()
    }

    private fun observeFavoriteState() {
        val currentDestinationId = destinationId ?: return

        val sharedObserver = observeFavoriteDestinationIdsUseCase
        if (sharedObserver != null) {
            viewModelScope.launch {
                sharedObserver()
                    .map { ids -> currentDestinationId in ids }
                    .distinctUntilChanged()
                    .collect { isFavorite ->
                        _uiState.update { it.copy(isFavorite = isFavorite) }
                    }
            }
            return
        }

        viewModelScope.launch {
            getAllPlacesUseCase()
                .map { places -> places.any { it.id == currentDestinationId } }
                .distinctUntilChanged()
                .collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
        }
    }

    private fun loadDestination() {
        val currentDestinationId = destinationId
        if (currentDestinationId == null) {
            _uiState.value = _uiState.value.copy(detailState = UiState.Error("Destination not found"))
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(detailState = UiState.Loading)

            when (val result = getDestinationByIdUseCase(currentDestinationId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(detailState = UiState.Success(result.data))
                    loadRelatedDestinations(result.data)
                }
                is Result.Error -> {
                    // Handling Not Found vs Error later (Task US3), but for now generic Error
                    _uiState.value = _uiState.value.copy(detailState = UiState.Error("Could not load destination details"))
                }
            }
        }
    }

    private fun loadRelatedDestinations(destination: com.example.domain.model.destination.Destination) {
        val interestId = destination.interests.firstOrNull()?.interestID ?: return // If no interest, skip

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(relatedDestinationsState = UiState.Loading)
            when (
                val result = getAllDestinationsUseCase(
                    pageIndex = 1,
                    pageSize = RELATED_PAGE_SIZE,
                    cityId = null,
                    interestId = interestId
                )
            ) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        relatedDestinationsState = UiState.Success(
                            buildRelatedDestinations(result.data, destination.destinationID, interestId)
                        )
                    )
                }

                is Result.Error -> {
                    if (result.error == DataError.Network.Timeout) {
                        // Fallback: avoid blocking UI on slow category-filter endpoint.
                        when (
                            val fallback = getAllDestinationsUseCase(
                                pageIndex = 1,
                                pageSize = RELATED_PAGE_SIZE,
                                cityId = null,
                                interestId = null
                            )
                        ) {
                            is Result.Success -> {
                                _uiState.value = _uiState.value.copy(
                                    relatedDestinationsState = UiState.Success(
                                        buildFallbackRelatedDestinations(
                                            fallback.data,
                                            destination.destinationID,
                                            interestId
                                        )
                                    )
                                )
                            }

                            is Result.Error -> {
                                _uiState.value = _uiState.value.copy(
                                    relatedDestinationsState = UiState.Success(emptyList())
                                )
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            relatedDestinationsState = UiState.Error("Failed to load related destinations")
                        )
                    }
                }
            }
        }
    }

    private fun buildRelatedDestinations(
        source: List<com.example.domain.model.destination.Destination>,
        currentDestinationId: Int,
        interestId: Int
    ): List<com.example.domain.model.destination.Destination> {
        return source
            .filter { candidate ->
                candidate.destinationID != currentDestinationId &&
                    candidate.interests.any { it.interestID == interestId }
            }
            .sortedWith(
                compareByDescending<com.example.domain.model.destination.Destination> { it.rating }
                    .thenByDescending { it.totalReviews }
                    .thenBy { it.destinationID }
            )
            .take(10)
    }

    private fun buildFallbackRelatedDestinations(
        source: List<com.example.domain.model.destination.Destination>,
        currentDestinationId: Int,
        preferredInterestId: Int
    ): List<com.example.domain.model.destination.Destination> {
        val strictMatches = buildRelatedDestinations(source, currentDestinationId, preferredInterestId)
        if (strictMatches.isNotEmpty()) return strictMatches

        return source
            .filter { candidate -> candidate.destinationID != currentDestinationId }
            .sortedWith(
                compareByDescending<com.example.domain.model.destination.Destination> { candidate ->
                    candidate.interests.count { it.interestID == preferredInterestId }
                }
                    .thenByDescending { it.rating }
                    .thenByDescending { it.totalReviews }
                    .thenBy { it.destinationID }
            )
            .take(10)
    }

    fun onAction(action: DestinationDetailAction) {
        when (action) {
            is DestinationDetailAction.OnBackClicked -> {
                viewModelScope.launch { _events.send(DestinationDetailEvent.NavigateBack) }
            }
            is DestinationDetailAction.OnRetry -> {
                loadDestination()
            }
            is DestinationDetailAction.OnRetryReviews -> {
                retryReviews()
            }
            is DestinationDetailAction.OnFavoriteClicked -> toggleFavorite()
            is DestinationDetailAction.OnViewOnMapClicked -> openMap()
            is DestinationDetailAction.OnShareClicked -> shareMap()
            is DestinationDetailAction.OnImagePageChanged -> Unit
            is DestinationDetailAction.OnRetryRelatedDestinations -> retryRelatedDestinations()
            is DestinationDetailAction.OnRelatedDestinationClicked -> {
                viewModelScope.launch {
                    _events.send(DestinationDetailEvent.NavigateToDestination(action.destinationId))
                }
            }

            is DestinationDetailAction.OnReviewTextChanged -> {
                _uiState.update { it.copy(reviewText = action.text) }
            }

            is DestinationDetailAction.OnReviewRatingChanged -> {
                _uiState.update { it.copy(reviewRating = action.rating) }
            }

            DestinationDetailAction.OnSubmitReviewClicked -> {
                submitReview()
            }

            is DestinationDetailAction.OnDeleteReviewClicked -> {
                deleteReview()
            }
        }
    }

    private fun toggleFavorite() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                if (_uiState.value.isFavoriteMutationInFlight) return@launch

                val newState = !_uiState.value.isFavorite
                _uiState.value = _uiState.value.copy(
                    isFavorite = newState,
                    isFavoriteMutationInFlight = true
                )

                val result = if (newState) {
                    if (addDestinationFavoriteUseCase != null) {
                        addDestinationFavoriteUseCase(destination.destinationID)
                    } else {
                        when (favoritePlaceUseCase(destination.toPlace())) {
                            is Result.Success -> Result.Success(
                                FavoriteMutationResult(
                                    isSuccess = true,
                                    message = null,
                                    errors = emptyList()
                                )
                            )

                            is Result.Error -> Result.Error(DataError.Local.DatabaseError)
                        }
                    }
                } else {
                    if (removeDestinationFavoriteUseCase != null) {
                        removeDestinationFavoriteUseCase(destination.destinationID)
                    } else {
                        when (favoritePlaceUseCase(destination.toPlace())) {
                            is Result.Success -> Result.Success(
                                FavoriteMutationResult(
                                    isSuccess = true,
                                    message = null,
                                    errors = emptyList()
                                )
                            )

                            is Result.Error -> Result.Error(DataError.Local.DatabaseError)
                        }
                    }
                }

                when (result) {
                    is Result.Success -> {
                        val message = result.data.message
                            ?.takeIf { it.isNotBlank() }
                            ?: if (newState) {
                                "Saved to favourites"
                            } else {
                                "Removed from favourites"
                            }
                        _events.send(DestinationDetailEvent.ShowSuccessSnackbar(message))
                    }
                    is Result.Error -> {
                        // Revert
                        _uiState.value = _uiState.value.copy(isFavorite = !newState)
                        _events.send(DestinationDetailEvent.ShowErrorSnackbar(mapFavoriteMutationError(result.error)))
                    }
                }

                _uiState.value = _uiState.value.copy(isFavoriteMutationInFlight = false)
            }
        }
    }

    private fun mapFavoriteMutationError(error: DataError): String {
        return when (error) {
            DataError.Network.NoInternetConnection -> "No internet connection. Check your network and try again."
            DataError.Network.Timeout -> "Request timed out. Please try again."
            DataError.Network.BadRequest,
            DataError.Network.ServerError,
            DataError.Network.UnexpectedResponse,
            DataError.Network.TooManyRequests -> "Server error while updating favorites. Please try again."
            DataError.Validation.InvalidInputs,
            DataError.Data.NotFound -> "This destination is no longer available for favorites."
            else -> "Could not update favorites. Please try again."
        }
    }

    private fun openMap() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                _events.send(DestinationDetailEvent.OpenMap(destination.latitude, destination.longitude))
            }
        }
    }

    private fun shareMap() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            val destination = detailState.data
            viewModelScope.launch {
                _events.send(DestinationDetailEvent.ShareDestination("Check out ${destination.name} in ${destination.cityName}!"))
            }
        }
    }

    private fun retryRelatedDestinations() {
        val detailState = _uiState.value.detailState
        if (detailState is UiState.Success) {
            loadRelatedDestinations(detailState.data)
        }
    }
}
