package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Post
import ui.state.UiState

data class FavoriteState(
    val selectedItem: Int = 1,
    val selectedTab: SectionTab = SectionTab.All,
    val places: List<Place> = emptyList(),
    val posts: List<Post> = emptyList(),
    val placesUiState: UiState<Unit> = UiState.Idle,
    val postsUiState: UiState<Unit> = UiState.Idle,
) {
    val totalFavoriteCount: Int get() = places.size + posts.size

    val displayedPlaces: List<Place>
        get() = when (selectedTab) {
            SectionTab.All, SectionTab.Places -> places
            SectionTab.Posts -> emptyList()
        }

    val displayedPosts: List<Post>
        get() = when (selectedTab) {
            SectionTab.All, SectionTab.Posts -> posts
            SectionTab.Places -> emptyList()
        }

    val isEmpty: Boolean
        get() = when (selectedTab) {
            SectionTab.All -> places.isEmpty() && posts.isEmpty()
            SectionTab.Places -> places.isEmpty()
            SectionTab.Posts -> posts.isEmpty()
        }
}
