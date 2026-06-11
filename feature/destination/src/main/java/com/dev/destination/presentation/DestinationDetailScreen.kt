package com.dev.destination.presentation

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dev.destination.R
import com.dev.destination.components.AboutSection
import com.dev.destination.components.AnotherDestinationsRow
import com.dev.destination.components.DestinationLocationSection
import com.dev.destination.components.DestinationReviewsSection
import com.dev.destination.components.DetailErrorState
import com.dev.destination.components.DetailLoadingState
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.favorite
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest
import com.example.domain.model.review.Review
import kotlinx.coroutines.launch
import java.time.Instant

@Composable
fun DestinationDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDestination: (Int) -> Unit,
    onOpenMap: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DestinationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is DestinationDetailEvent.NavigateBack -> onNavigateBack()
                is DestinationDetailEvent.NavigateToDestination -> onNavigateToDestination(event.destinationId)
                is DestinationDetailEvent.OpenMap -> onOpenMap(event.lat, event.lng)
                is DestinationDetailEvent.ShowSuccessSnackbar -> snackbarHostState.showSnackbar(event.msg.asString(context))
                is DestinationDetailEvent.ShowErrorSnackbar -> snackbarHostState.showSnackbar(event.msg.asString(context))
                is DestinationDetailEvent.ShareDestination -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, event.text)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.destination_share_title)
                        )
                    )
                }
            }
        }
    }

    DestinationDetailContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun DestinationDetailContent(
    uiState: DestinationDetailUiState,
    onAction: (DestinationDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val overlayContentColor = Color.White

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (val detailState = uiState.detailState) {
            is UiState.Success -> {
                val destination = detailState.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero Image & Overlay Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.xxxl * 6 + MaterialTheme.spacing.sm)
                    ) {
                        // Background Image Pager
                        if (destination.imageUrls.isNotEmpty()) {
                            val pagerState = androidx.compose.foundation.pager.rememberPagerState(
                                pageCount = { destination.imageUrls.size }
                            )
                            val scope = androidx.compose.runtime.rememberCoroutineScope()

                            androidx.compose.foundation.pager.HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val imageUrl = destination.imageUrls[page]
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .placeholder(com.example.designsystem.R.drawable.image_placeholder)
                                        .error(com.example.designsystem.R.drawable.image_placeholder)
                                        .build(),
                                    contentDescription = stringResource(id = R.string.destination_hero_image_cd),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            if (destination.imageUrls.size > 1) {
                                PagerPillIndicator(imageUrls = destination.imageUrls, pagerState = pagerState)
                                PagerLeftArrow(pagerState = pagerState, scope = scope)
                                PagerRightArrow(pagerState = pagerState, imageUrls = destination.imageUrls, scope = scope)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }

                        // Gradient Shadow at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
                                        ),
                                        // Starts below the middle
                                        startY = 500f
                                    )
                                )
                        )

                        // Top Top Actions (Back & Favorite)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = MaterialTheme.spacing.md,
                                    top = MaterialTheme.spacing.xs,
                                    end = MaterialTheme.spacing.md
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.size(MaterialTheme.spacing.xxxl)
                            ) {
                                IconButton(onClick = { onAction(DestinationDetailAction.OnBackClicked) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(id = R.string.destination_back_cd),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.size(MaterialTheme.spacing.xxxl)
                            ) {
                                IconButton(
                                    enabled = !uiState.isFavoriteMutationInFlight,
                                    onClick = { onAction(DestinationDetailAction.OnFavoriteClicked) }
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = stringResource(id = R.string.destination_favorite_cd),
                                        tint = if (uiState.isFavorite) {
                                            MaterialTheme.colorScheme.favorite
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                                    )
                                }
                            }
                        }

                        // Text Content at Bottom Left
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(MaterialTheme.spacing.md)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = stringResource(id = R.string.destination_location_cd),
                                    tint = overlayContentColor,
                                    modifier = Modifier.size(MaterialTheme.spacing.md)
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                                Text(
                                    // Fallback text acting as subtitle category
                                    text = destination.interests.firstOrNull()?.interestName
                                        ?: stringResource(id = R.string.destination_default_interest),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = overlayContentColor.copy(alpha = 0.9f)
                                )
                            }

                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

                            Text(
                                text = "${destination.name}, ${destination.cityName}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = overlayContentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                            // Rating Pill Container
                            val displayedSummary = uiState.reviewSummary
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = stringResource(id = R.string.destination_rating_cd),
                                        tint = MaterialTheme.colorScheme.star,
                                        modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                                    )
                                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                                    Text(
                                        text =
                                        stringResource(
                                            id = R.string.destination_reviews_count,
                                            "${"%.1f".format(java.util.Locale.US, destination.rating)} " +
                                                "(${displayedSummary?.totalReviews ?: destination.totalReviews})"
                                        ),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = overlayContentColor
                                    )
                                }
                            }
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.md)
                    ) {
                        AboutSection(
                            description = destination.description,
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)
                        )

                        DestinationLocationSection(
                            destination = destination,
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)
                        )

                        DestinationReviewsSection(
                            uiState = uiState,
                            onAction = onAction,
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.lg)
                        )

                        RelatedDestinationsStateHandling(
                            state = uiState.relatedDestinationsState,
                            onRetry = { onAction(DestinationDetailAction.OnRetryRelatedDestinations) },
                            onDestinationClick = { destinationId ->
                                onAction(DestinationDetailAction.OnRelatedDestinationClicked(destinationId))
                            },
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.md)
                        )
                    }
                }
            }
            is UiState.Loading -> {
                DetailLoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is UiState.Error -> {
                DetailErrorState(
                    message = detailState.message,
                    onRetry = { onAction(DestinationDetailAction.OnRetry) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun RelatedDestinationsStateHandling(
    state: UiState<List<Destination>>,
    onRetry: () -> Unit,
    onDestinationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        UiState.Idle -> Unit
        UiState.Loading -> RelatedDestinationsLoadingSection(modifier = modifier)
        is UiState.Error -> RelatedDestinationsErrorSection(
            onRetry = onRetry,
            modifier = modifier
        )

        is UiState.Success -> {
            if (state.data.isNotEmpty()) {
                AnotherDestinationsRow(
                    destinations = state.data,
                    onDestinationClick = onDestinationClick,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun RelatedDestinationsLoadingSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.destination_suggested_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            items(items = listOf(1, 2, 3), key = { it }) {
                RelatedDestinationLoadingCard()
            }
        }
    }
}

@Composable
private fun RelatedDestinationLoadingCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(MaterialTheme.spacing.xxxl * 4 + MaterialTheme.spacing.xl + MaterialTheme.spacing.xs)
            .height(MaterialTheme.spacing.xxxl * 7 + MaterialTheme.spacing.xxs),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        border = BorderStroke(MaterialTheme.spacing.xxs, MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(MaterialTheme.spacing.xlg)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun RelatedDestinationsErrorSection(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.destination_suggested_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.destination_failed_to_load),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            Text(
                text = stringResource(id = R.string.destination_tap_to_retry),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
            OutlinedButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spacing.md)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs + (MaterialTheme.spacing.xxs / 2)))
                Text(text = stringResource(id = R.string.destination_retry))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationDetailScreenPreview() {
    val sampleDestination = Destination(
        cityName = "Paris",
        description = "Paris, France's capital, is a major European city and a global center" +
            " for art, fashion, gastronomy and culture. Its 19th-century " +
            "cityscape is crisscrossed by wide boulevards and the River Seine. " +
            "Beyond such landmarks as the Eiffel Tower and the 12th-century," +
            " Gothic Notre-Dame cathedral, the city is known for its cafe culture" +
            " and designer boutiques along the Rue du Faubourg Saint-Honoré.",
        destinationID = 1,
        imageUrls = listOf("https://images.unsplash.com/photo-1502602898657-3e91760cbb34"),
        interests = listOf(Interest(1, "Museums")),
        latitude = 48.8566,
        longitude = 2.3522,
        name = "Eiffel Tower",
        rating = 4.8,
        totalReviews = 1240
    )

    val sampleUiState = DestinationDetailUiState(
        detailState = UiState.Success(sampleDestination),
        isFavorite = true,
        reviewsState = UiState.Success(
            listOf(
                Review(
                    id = 1,
                    authorName = "Sarah Anderson",
                    authorAvatarUrl = null,
                    rating = 5,
                    content = "Cairo exceeded all my expectations! The pyramids are even more impressive in person.",
                    createdAt = Instant.now(),
                    helpfulCount = 142
                ),
                Review(
                    id = 2,
                    authorName = "Mohamed Ali",
                    authorAvatarUrl = null,
                    rating = 5,
                    content = "The history here is unmatched. Walking through Khan el-Khalili at night was magical.",
                    createdAt = Instant.now(),
                    helpfulCount = 98
                )
            )
        )
    )

    TravioTheme {
        Scaffold { padding ->
            DestinationDetailContent(
                uiState = sampleUiState,
                onAction = {},
                modifier = Modifier.padding(padding),
                snackbarHostState = remember { SnackbarHostState() }
            )
        }
    }
}

@Composable
private fun BoxScope.PagerLeftArrow(
    pagerState: androidx.compose.foundation.pager.PagerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val showLeft by remember { derivedStateOf { pagerState.currentPage > 0 } }
    if (showLeft) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = MaterialTheme.spacing.xs)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.pager_previous_cd),
                tint = Color.White,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs / 2)
            )
        }
    }
}

@Composable
private fun BoxScope.PagerRightArrow(
    pagerState: androidx.compose.foundation.pager.PagerState,
    imageUrls: List<String>,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val showRight by remember { derivedStateOf { pagerState.currentPage < imageUrls.size - 1 } }
    if (showRight) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = MaterialTheme.spacing.xs)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .size(MaterialTheme.spacing.xl + MaterialTheme.spacing.xxs)

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.pager_next_cd),
                tint = Color.White,
                modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs / 2)
            )
        }
    }
}

@Composable
private fun BoxScope.PagerPillIndicator(
    imageUrls: List<String>,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    Row(
        modifier = Modifier
            .height(MaterialTheme.spacing.lg)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = MaterialTheme.spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(imageUrls.size) { iteration ->
            val isSelected = pagerState.currentPage == iteration
            val width by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (isSelected) MaterialTheme.spacing.lg else MaterialTheme.spacing.xs,
                label = "indicator_width"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.xxs)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                    .width(width)
                    .height(MaterialTheme.spacing.xs)
            )
        }
    }
}
