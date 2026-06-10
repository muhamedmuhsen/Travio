package com.example.feature.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.AppSnackBar
import com.example.designsystem.components.SnackBarType
import com.example.designsystem.components.showAppSnackbar
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.designsystem.theme.star
import com.example.feature.chat.R
import com.example.feature.chat.presentation.viewmodel.ActivityItem
import com.example.feature.chat.presentation.viewmodel.DayItinerary
import com.example.feature.chat.presentation.viewmodel.RecommendedHotel
import com.example.feature.chat.presentation.viewmodel.TripDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is com.example.feature.chat.presentation.state.TripDetailUiEvent.ShowSnackbar -> {
                    snackbarHostState.showAppSnackbar(
                        message = event.message.asString(context),
                        type = SnackBarType.ERROR
                    )
                }
            }
        }
    }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            onNavigateBack()
        }
    }

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.delete_trip_title)) },
            text = { Text(stringResource(R.string.delete_trip_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTrip() }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (uiState.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.showDeleteDialog() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Trip")
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedItem = 3,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navigateToHome()
                        1 -> navigateToFavorite()
                        2 -> navigateToCommunity()
                        4 -> navigateToProfile()
                    }
                }
            )
        },
        snackbarHost = { AppSnackBar(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(uiState.error ?: stringResource(R.string.unknown_error))
            }
        } else {
            val selectedDay = uiState.days.find { it.id == uiState.selectedDayId }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    top = MaterialTheme.spacing.md,
                    bottom = MaterialTheme.spacing.xl
                )
            ) {
                // Day Tabs
                item {
                    DaySelectorTabs(
                        days = uiState.days,
                        selectedDayId = uiState.selectedDayId,
                        onDaySelected = { viewModel.selectDay(it) }
                    )
                }

                selectedDay?.let { day ->
                    // Hotels
                    if (day.recommendedHotels.isNotEmpty()) {
                        item {
                            RecommendedHotelsSection(hotels = day.recommendedHotels)
                        }
                    }

                    // Timeline Title
                    item {
                        Text(
                            text = day.subtitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm)
                        )
                    }

                    // Timeline
                    items(
                        items = day.activities,
                        key = { it.id }
                    ) { activity ->
                        val index = day.activities.indexOf(activity)
                        val isFirst = index == 0
                        val isLast = index == day.activities.size - 1
                        TimelineActivityNode(
                            activity = activity,
                            isFirst = isFirst,
                            isLast = isLast
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DaySelectorTabs(
    days: List<DayItinerary>,
    selectedDayId: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        items(days, key = { it.id }) { day ->
            val isSelected = day.id == selectedDayId
            Surface(
                shape = CircleShape,
                color = if (isSelected) {
                    Color(0xFF006D77)
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onDaySelected(day.id) }
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.lg,
                        vertical = MaterialTheme.spacing.sm
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = day.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedHotelsSection(
    hotels: List<RecommendedHotel>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = MaterialTheme.spacing.md)) {
        Text(
            text = stringResource(R.string.recommended_hotels),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md).padding(bottom = MaterialTheme.spacing.sm)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            items(hotels, key = { it.id }) { hotel ->
                HotelRecommendationCard(hotel = hotel)
            }
        }
    }
}

@Composable
fun HotelRecommendationCard(
    hotel: RecommendedHotel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(MaterialTheme.spacing.xxxl * 4 + MaterialTheme.spacing.lg + MaterialTheme.spacing.xxs)
            .height(
                MaterialTheme.spacing.xxxl * 3 + MaterialTheme.spacing.xl + MaterialTheme.spacing.xs
            ),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.sm
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = hotel.imageUrl
                        ?: com.example.designsystem.R.drawable.image_placeholder,
                    contentDescription = hotel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = MaterialTheme.spacing.md,
                                topEnd = MaterialTheme.spacing.md
                            )
                        )
                )

                Surface(
                    shape = RoundedCornerShape(MaterialTheme.spacing.sm),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MaterialTheme.spacing.sm)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.xs,
                            vertical = MaterialTheme.spacing.xxs
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.star,
                            modifier = Modifier.size(MaterialTheme.spacing.md)
                        )
                        Text(
                            text = hotel.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.sm)
            ) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xxs)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(
                            MaterialTheme.spacing.md - MaterialTheme.spacing.xxs / 2
                        )
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxs))
                    Text(
                        text = hotel.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineActivityNode(
    activity: ActivityItem,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val activityIcon = when {
        activity.tag.contains("breakfast", ignoreCase = true) -> Icons.Filled.BreakfastDining
        activity.tag.contains("lunch", ignoreCase = true) -> Icons.Filled.LunchDining
        activity.tag.contains("dinner", ignoreCase = true) -> Icons.Filled.DinnerDining
        activity.tag.contains("attraction", ignoreCase = true) -> Icons.Filled.Attractions
        activity.tag.contains("activity", ignoreCase = true) -> Icons.Filled.DirectionsWalk
        activity.tag.contains("explore", ignoreCase = true) -> Icons.Filled.Explore
        activity.tag.contains("museum", ignoreCase = true) -> Icons.Filled.Museum
        activity.tag.contains(
            "restaurant",
            ignoreCase = true
        ) || activity.tag.contains(
            "food",
            ignoreCase = true
        ) -> Icons.Filled.Restaurant
        activity.tag.contains(
            "cafe",
            ignoreCase = true
        ) || activity.tag.contains(
            "coffee",
            ignoreCase = true
        ) -> Icons.Filled.LocalCafe
        else -> Icons.Default.LocationOn
    }

    val strokeWidthDp = MaterialTheme.elevation.sm
    val leftColumnWidth = 72.dp
    val xDp = leftColumnWidth / 2
    val iconSize = 32.dp
    val innerIconSize = 16.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.xs)
            .drawBehind {
                val strokeWidth = strokeWidthDp.toPx()
                val x = xDp.toPx()
                // Top padding is 16dp, icon size is 32dp, center is 16 + 16 = 32dp
                val iconCenterY = (16.dp + (iconSize / 2)).toPx()
                // Top padding (16) + icon (32) + spacer (8) + approx text height (20)
                val textBottomY = (16.dp + iconSize + 8.dp + 20.dp).toPx()

                val startY = if (isFirst) textBottomY else 0f
                val endY = if (isLast) iconCenterY else size.height

                drawLine(
                    color = lineColor,
                    start = Offset(x, startY),
                    end = Offset(x, endY),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        // Left Column (Timeline Icon and Time)
        Column(
            modifier = Modifier
                .width(leftColumnWidth)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activityIcon,
                    contentDescription = activity.tag,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(innerIconSize)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = activity.time,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 2.dp, horizontal = 4.dp)
            )
        }

        // Right Column (Activity Card)
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp, end = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Square Image
                AsyncImage(
                    model = activity.imageUrl
                        ?: com.example.designsystem.R.drawable.image_placeholder,
                    contentDescription = activity.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                // Details Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Category Tag
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Text(
                            text = activity.tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Title
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = MaterialTheme.typography.bodySmall.fontSize * 1.2
                    )
                }
            }
        }
    }
}
