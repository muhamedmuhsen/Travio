package com.dev.destination.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.R
import com.dev.destination.presentation.DestinationDetailAction
import com.dev.destination.presentation.DestinationDetailUiState
import com.dev.destination.presentation.UiState
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.domain.model.review.Review
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DestinationReviewsSection(
    uiState: DestinationDetailUiState,
    onAction: (DestinationDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.destination_reviews_and_ratings),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Rating Summary
            val detailState = uiState.detailState as? UiState.Success
            val averageRatingDouble = detailState?.data?.rating ?: uiState.reviewSummary?.averageRating?.toDouble() ?: 0.0
            val totalReviews = detailState?.data?.totalReviews ?: uiState.reviewSummary?.totalReviews ?: 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val ratingInt = averageRatingDouble.toInt()
                    Text(
                        text = if (averageRatingDouble > 0) "%.1f".format(java.util.Locale.US, averageRatingDouble) else "0.0",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        repeat(5) { index ->
                            val starRating = index + 1
                            val isFilled = starRating <= ratingInt
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isFilled) {
                                    MaterialTheme.colorScheme.star
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                                modifier = Modifier.size(MaterialTheme.spacing.md)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.destination_reviews_count, totalReviews.toString()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            // Review Submission (Integrated)
            ReviewSubmissionCard(
                text = uiState.reviewText,
                rating = uiState.reviewRating,
                isSubmitting = uiState.isSubmittingReview,
                onTextChanged = { onAction(DestinationDetailAction.OnReviewTextChanged(it)) },
                onRatingChanged = { onAction(DestinationDetailAction.OnReviewRatingChanged(it)) },
                onSubmitClicked = { onAction(DestinationDetailAction.OnSubmitReviewClicked) },
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.lg)
            )

            // Reviews List
            when (val state = uiState.reviewsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.destination_no_reviews),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        val reviewsToShow = if (isExpanded) state.data else state.data.take(4)
                        reviewsToShow.forEachIndexed { index, review ->
                            DestinationReviewItem(
                                review = review,
                                onDelete = if (review.isOwnedByCurrentUser) {
                                    { onAction(DestinationDetailAction.OnDeleteReviewClicked) }
                                } else {
                                    null
                                }
                            )
                            if (index < reviewsToShow.size - 1) {
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                            }
                        }

                        if (state.data.size > 4) {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                            OutlinedButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(MaterialTheme.spacing.xs),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                            ) {
                                Text(
                                    text = stringResource(
                                        if (isExpanded) {
                                            R.string.destination_show_less_reviews
                                        } else {
                                            R.string.destination_show_more_reviews
                                        }
                                    )
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun DestinationReviewItem(
    review: Review,
    onDelete: (() -> Unit)? = null
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.spacing.xxl)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (review.authorAvatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(review.authorAvatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = review.authorName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.authorName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val dateText = review.createdAt.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) {
                            MaterialTheme.colorScheme.star
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.size(MaterialTheme.spacing.sm)
                    )
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(MaterialTheme.spacing.xl)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_review),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSubmissionCard(
    text: String,
    rating: Int,
    isSubmitting: Boolean,
    onTextChanged: (String) -> Unit,
    onRatingChanged: (Int) -> Unit,
    onSubmitClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InteractiveRatingBar(
                rating = rating,
                onRatingChanged = onRatingChanged,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)
            )

            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = { Text(stringResource(id = R.string.destination_reviews_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.xxs),
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            Button(
                onClick = onSubmitClicked,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(MaterialTheme.spacing.sm),
                enabled = !isSubmitting && text.isNotBlank() && rating > 0
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.spacing.lg),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = com.example.designsystem.R.string.next))
                }
            }
        }
    }
}

@Composable
private fun InteractiveRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starCount: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        repeat(starCount) { index ->
            val starRating = index + 1
            Icon(
                imageVector = if (rating >= starRating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (rating >= starRating) MaterialTheme.colorScheme.star else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xl)
                    .clickable { onRatingChanged(starRating) }
            )
        }
    }
}
