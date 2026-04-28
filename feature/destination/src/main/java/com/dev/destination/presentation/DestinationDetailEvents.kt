package com.dev.destination.presentation

sealed interface DestinationDetailAction {
    data object OnBackClicked : DestinationDetailAction
    data object OnShareClicked : DestinationDetailAction
    data object OnFavoriteClicked : DestinationDetailAction
    data object OnViewOnMapClicked : DestinationDetailAction
    data object OnRetry : DestinationDetailAction
    data object OnRetryReviews : DestinationDetailAction
    data object OnRetryRelatedDestinations : DestinationDetailAction
    data class OnImagePageChanged(val index: Int) : DestinationDetailAction
    data class OnRelatedDestinationClicked(val destinationId: Int) : DestinationDetailAction
    data class OnReviewTextChanged(val text: String) : DestinationDetailAction
    data class OnReviewRatingChanged(val rating: Int) : DestinationDetailAction
    data object OnSubmitReviewClicked : DestinationDetailAction
    data object OnDeleteReviewClicked : DestinationDetailAction
}

sealed interface DestinationDetailEvent {
    data object NavigateBack : DestinationDetailEvent
    data class NavigateToDestination(val destinationId: Int) : DestinationDetailEvent
    data class OpenMap(val lat: Double, val lng: Double) : DestinationDetailEvent
    data class ShareDestination(val text: String) : DestinationDetailEvent
    data class ShowSuccessSnackbar(val msg: String) : DestinationDetailEvent
    data class ShowErrorSnackbar(val msg: String) : DestinationDetailEvent
}
