package com.example.feature.chat.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.trip.TripItem
import com.example.feature.chat.R
import com.example.feature.chat.presentation.viewmodel.TripsUiState
import com.example.feature.chat.presentation.viewmodel.TripsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

data class TripUiModel(
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateToAiChat: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    navigateToHome: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            item {
                Text(
                    text = stringResource(R.string.my_previous_trips),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.md, top = MaterialTheme.spacing.sm)
                )
            }

            item {
                AiPlanningCard(onPlanClick = onNavigateToAiChat)
            }

            when (uiState) {
                is TripsUiState.Loading -> {
                    item {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.padding(MaterialTheme.spacing.md).fillMaxWidth()
                        )
                    }
                }
                is TripsUiState.Error -> {
                    item {
                        Text(text = (uiState as TripsUiState.Error).message)
                    }
                }
                is TripsUiState.Success -> {
                    val trips = (uiState as TripsUiState.Success).trips
                    items(trips, key = { it.id }) { trip ->
                        TripCard(
                            trip = trip,
                            onClick = { onNavigateToTripDetail(trip.id.toString()) },
                            onRemove = {
                                viewModel.deleteTrip(trip.id.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiPlanningCard(
    onPlanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(MaterialTheme.elevation.xs, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.none)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illustration Cluster
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl * 2)
                    .padding(bottom = MaterialTheme.spacing.md),
                contentAlignment = Alignment.Center
            ) {
                // Main map circle
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.md)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(MaterialTheme.spacing.xl)
                    )
                }

                // Airplane small circle (bottom left)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(MaterialTheme.spacing.xlg)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = com.example.designsystem.R.drawable.ic_airplane),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                }

                // Sparkle small circle (top right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(MaterialTheme.spacing.xlg)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                }
            }

            Text(
                text = stringResource(R.string.ai_powered_planning),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs)
            )

            Text(
                text = stringResource(R.string.ai_planning_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.lg)
            )

            Button(
                onClick = onPlanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                Text(
                    text = stringResource(R.string.plan_with_ai),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun TripCard(
    trip: TripItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.sm),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = trip.cityHeroImage.ifEmpty { null } ?: com.example.designsystem.R.drawable.image_placeholder,
                contentDescription = trip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.xxs)
                    .fillMaxSize()
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.spacing.sm),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = trip.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.lg)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.spacing.md),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val formattedDate = remember(trip.createdAt) {
                            try {
                                val datePart = trip.createdAt.substringBefore('T')
                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePart)
                                date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it) } ?: trip.createdAt
                            } catch (e: Exception) {
                                trip.createdAt
                            }
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val tripDays = trip.totalDays
                    Text(
                        text = stringResource(R.string.days_trip_count, tripDays),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(MaterialTheme.spacing.xl)
                        .padding(MaterialTheme.spacing.xxs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiPlanningCardPreview() {
    TravioTheme {
        Box(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            AiPlanningCard(onPlanClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripsScreenPreview() {
    TravioTheme {
        TripsScreen(
            onNavigateToAiChat = {},
            onNavigateToTripDetail = {},
            navigateToHome = {},
            navigateToFavorite = {},
            navigateToCommunity = {},
            navigateToProfile = {}
        )
    }
}
