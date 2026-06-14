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
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
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
                    MaterialTheme.colorScheme.primary
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
    val context = LocalContext.current
    Card(
        modifier = modifier
            .width(MaterialTheme.spacing.xxxl * 5),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.sm
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = MaterialTheme.elevation.xs,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = hotel.imageUrl
                    ?: com.example.designsystem.R.drawable.image_placeholder,
                contentDescription = hotel.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = MaterialTheme.spacing.xs)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        val ratingInt = hotel.rating.toInt()
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < ratingInt) {
                                    MaterialTheme.colorScheme.star
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                },
                                modifier = Modifier.size(MaterialTheme.spacing.md)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MaterialTheme.spacing.md - MaterialTheme.spacing.xxs / 2)
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

                if (hotel.latitude != null && hotel.longitude != null) {
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            val encodedName = android.net.Uri.encode(hotel.name)
                            val geoQuery = "geo:${hotel.latitude},${hotel.longitude}" +
                                "?q=${hotel.latitude},${hotel.longitude}($encodedName)"
                            val uri = android.net.Uri.parse(geoQuery)
                            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                            context.startActivity(mapIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.spacing.sm)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Text(stringResource(R.string.view_on_map), style = MaterialTheme.typography.labelMedium)
                    }
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
    val context = LocalContext.current
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
    val leftColumnWidth = MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs
    val xDp = leftColumnWidth / 2
    val iconSize = MaterialTheme.spacing.xl
    val innerIconSize = MaterialTheme.spacing.md
    val topPadding = MaterialTheme.spacing.md

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md)
            .drawBehind {
                val strokeWidth = strokeWidthDp.toPx()
                val x = xDp.toPx()
                val iconCenterY = (topPadding + (iconSize / 2)).toPx()

                val startY = if (isFirst) iconCenterY else 0f
                val endY = if (isLast) iconCenterY else size.height

                drawLine(
                    color = lineColor,
                    start = Offset(x, startY),
                    end = Offset(x, endY),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        // Left Column (Timeline Icon ONLY)
        Column(
            modifier = Modifier
                .width(leftColumnWidth)
                .padding(top = MaterialTheme.spacing.md),
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

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = MaterialTheme.spacing.xxs / 2, horizontal = MaterialTheme.spacing.xxs)
            ) {
                val timeParts = activity.time.split(" ")
                if (timeParts.size >= 2) {
                    Text(
                        text = timeParts[0],
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = timeParts.drop(1).joinToString(" "),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = activity.time,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Right Column (Activity Card)
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(top = MaterialTheme.spacing.md, bottom = MaterialTheme.spacing.md),
            shape = RoundedCornerShape(MaterialTheme.spacing.md),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = MaterialTheme.elevation.xs
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = strokeWidthDp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top full-width image
                AsyncImage(
                    model = activity.imageUrl
                        ?: com.example.designsystem.R.drawable.image_placeholder,
                    contentDescription = activity.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl * 3)
                )

                // Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.md)
                ) {
                    // Tag row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = activity.tag.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(
                                    horizontal = MaterialTheme.spacing.sm,
                                    vertical = MaterialTheme.spacing.xs - MaterialTheme.spacing.xxs / 2
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))

                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.2
                    )

                    if (activity.latitude != null && activity.longitude != null) {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                        androidx.compose.material3.OutlinedButton(
                            onClick = {
                                val encodedTitle = android.net.Uri.encode(activity.title)
                                val geoQuery = "geo:${activity.latitude},${activity.longitude}" +
                                    "?q=${activity.latitude},${activity.longitude}($encodedTitle)"
                                val uri = android.net.Uri.parse(geoQuery)
                                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.spacing.md)
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                            Text(stringResource(R.string.view_on_map))
                        }
                    }
                }
            }
        }
    }
}
