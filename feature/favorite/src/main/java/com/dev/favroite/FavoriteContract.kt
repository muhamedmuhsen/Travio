package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.dev.utils.uitext.UiText

sealed interface FavoriteEvent {
    data object OnScreenOpened : FavoriteEvent

    data class OnTabSelected(val tab: SectionTab) : FavoriteEvent

    data object OnRetryCurrentTab : FavoriteEvent

    data object OnLoadMoreCurrentTab : FavoriteEvent

    data class OnUnfavoriteDestination(val destinationId: String) : FavoriteEvent

    data class OnUnfavoriteTrip(val tripId: String) : FavoriteEvent

    data class OnBottomNavSelected(val index: Int) : FavoriteEvent
}

sealed interface FavoriteEffect {
    data class ShowMessage(val message: UiText) : FavoriteEffect
}
