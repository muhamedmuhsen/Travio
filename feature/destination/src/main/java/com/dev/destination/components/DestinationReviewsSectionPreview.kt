package com.dev.destination.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.destination.presentation.DestinationDetailUiState
import com.dev.destination.presentation.UiState
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.review.Review
import java.time.Instant

@Preview(showBackground = true, name = "Reviews - Less than 4")
@Composable
fun DestinationReviewsSectionPreviewLess() {
    val reviews = List(3) { index ->
        Review(
            id = index,
            authorName = "User $index",
            authorAvatarUrl = null,
            rating = 4,
            content = "This is a sample review content for user $index.",
            createdAt = Instant.now(),
            helpfulCount = 0,
            isOwnedByCurrentUser = false
        )
    }
    val uiState = DestinationDetailUiState(
        reviewsState = UiState.Success(reviews)
    )
    TravioTheme {
        Surface {
            DestinationReviewsSection(
                uiState = uiState,
                onAction = {},
                modifier = Modifier.padding(MaterialTheme.spacing.md)
            )
        }
    }
}

@Preview(showBackground = true, name = "Reviews - More than 4")
@Composable
fun DestinationReviewsSectionPreviewMore() {
    val reviews = List(6) { index ->
        Review(
            id = index,
            authorName = "User $index",
            authorAvatarUrl = null,
            rating = 5,
            content = "This is a great place! Review $index",
            createdAt = Instant.now(),
            helpfulCount = 2,
            isOwnedByCurrentUser = false
        )
    }
    val uiState = DestinationDetailUiState(
        reviewsState = UiState.Success(reviews)
    )
    TravioTheme {
        Surface {
            DestinationReviewsSection(
                uiState = uiState,
                onAction = {},
                modifier = Modifier.padding(MaterialTheme.spacing.md)
            )
        }
    }
}
