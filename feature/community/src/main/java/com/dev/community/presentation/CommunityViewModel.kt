package com.dev.community.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState(posts = samplePosts()))
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    fun onLikeClicked(postId: Int) {
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            isLiked = !post.isLiked,
                            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                        )
                    } else {
                        post
                    }
                }
            )
        }
    }

    private fun samplePosts(): List<CommunityPost> =
        listOf(
            CommunityPost(
                id = 1,
                author = "Ahmed Ali",
                avatarUrl = "",
                location = "Santorini, Greece",
                timeAgo = "2 hours ago",
                content = "The sunset views from Oia are absolutely breathtaking. " +
                        "The blue domes against the golden hour light are magical.",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800",
                    "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?w=800",
                    "https://images.unsplash.com/photo-1601581975053-7c199b540f7e?w=800"
                ),
                likesCount = 245,
                commentsCount = 2,
                rating = 5f
            ),
            CommunityPost(
                id = 2,
                author = "Marcus Rodriguez",
                avatarUrl = "",
                location = "Bali, Indonesia",
                timeAgo = "5 hours ago",
                content = "Exploring the Tegallalang Rice Terraces at sunrise was like seeing the " +
                        "light of Bali for the first time. The cool morning mist and the sound " +
                        "of water flowing through the channels make this place absolutely serene.",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800"
                ),
                likesCount = 95,
                commentsCount = 17,
                rating = 4.5f
            ),
            CommunityPost(
                id = 3,
                author = "Mohamed Mohsen",
                avatarUrl = "",
                location = "Paris, France",
                timeAgo = "Yesterday",
                content = "My drone shot of the Eiffel Tower at dawn — France is more beautiful in person " +
                        "than any photo can capture. It was worth the early wake-up to beat the crowds.",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=800"
                ),
                likesCount = 215,
                commentsCount = 42,
                rating = 4f
            ),
            CommunityPost(
                id = 4,
                author = "Ahmed Ali",
                avatarUrl = "",
                location = "Goa, India",
                timeAgo = "2 days ago",
                content = "Watching this sunset from Palolem Beach is an explosion of bohemian colors, sharp contrasts.",
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
                ),
                likesCount = 76,
                commentsCount = 11,
                rating = 4.8f
            )
        )
}
