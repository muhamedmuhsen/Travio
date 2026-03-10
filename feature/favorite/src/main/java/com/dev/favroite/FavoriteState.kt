package com.dev.favroite

import com.dev.favroite.components.SectionTab
import com.dev.utils.uistate.UiState
import com.example.domain.model.favorite.Place
import com.example.domain.model.favorite.Post

data class FavoriteState(
    val selectedItem: Int = 1,
    val selectedTab: SectionTab = SectionTab.All,
    val Places: List<Place> = emptyList(),
    val posts: List<Post> = emptyList(),
    val placesUiState: UiState<Unit> = UiState.Idle,
    val postsUiState: UiState<Unit> = UiState.Idle
) {
    val totalFavoriteCount: Int get() = Places.size + posts.size

    val displayedPlaces: List<Place>
        get() = when (selectedTab) {
            SectionTab.All, SectionTab.Places -> Places
            SectionTab.Posts -> emptyList()
        }

    val displayedPosts: List<Post>
        get() = when (selectedTab) {
            SectionTab.All, SectionTab.Posts -> posts
            SectionTab.Places -> emptyList()
        }

    val isEmpty: Boolean
        get() = when (selectedTab) {
            SectionTab.All -> Places.isEmpty() && posts.isEmpty()
            SectionTab.Places -> Places.isEmpty()
            SectionTab.Posts -> posts.isEmpty()
        }
}
