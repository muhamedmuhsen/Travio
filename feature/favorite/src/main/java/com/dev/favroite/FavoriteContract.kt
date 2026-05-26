package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText

sealed interface FavoriteEvent {
    data object OnScreenOpened : FavoriteEvent

    data object OnLoadDestinations : FavoriteEvent

    data object OnLoadMoreDestinations : FavoriteEvent

    data object OnRetryDestinations : FavoriteEvent

    data object OnRetryLoadMoreDestinations : FavoriteEvent

    data class OnTabSelected(val tab: SectionTab) : FavoriteEvent

    data object OnRetryCurrentTab : FavoriteEvent

    data object OnLoadMoreCurrentTab : FavoriteEvent

    data class OnUnfavoriteDestination(val destinationId: String) : FavoriteEvent

    data class OnFavoriteTripToggled(val tripId: Int, val isFavorite: Boolean) : FavoriteEvent

    data class OnTripItemVisible(val index: Int) : FavoriteEvent

    data class OnBottomNavSelected(val index: Int) : FavoriteEvent
}

sealed interface FavoriteEffect {
    data class ShowMessage(val message: UiText) : FavoriteEffect

    data object ScrollToTop : FavoriteEffect
}
