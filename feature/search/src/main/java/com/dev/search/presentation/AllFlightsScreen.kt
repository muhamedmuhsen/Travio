package com.dev.search.presentation

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.search.R

@Immutable
data class FlightFieldUi(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val value: String
)

@Immutable
data class FlightCardUi(
    val airline: String,
    val flightCode: String,
    val departureTime: String,
    val departureCode: String,
    val departureCity: String,
    val duration: String,
    val arrivalTime: String,
    val arrivalCode: String,
    val arrivalCity: String,
    val price: String,
    val status: String
)

@Immutable
data class AllFlightsData(
    val title: String,
    val fields: List<FlightFieldUi>,
    val flightCard: FlightCardUi
)

sealed interface AllFlightsUiState {
    data object Loading : AllFlightsUiState
    data class Success(val data: AllFlightsData) : AllFlightsUiState
    data class Error(val message: String) : AllFlightsUiState
}

private enum class BottomNavTab {
    Explore,
    Favorite,
    Community,
    ChatAi,
    Profile
}

@Composable
fun AllFlightsScreen(
    modifier: Modifier = Modifier,
    uiState: AllFlightsUiState = AllFlightsUiState.Success(sampleAllFlightsData()),
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onBookNowClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(BottomNavTab.Explore) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            AllFlightsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AllFlightsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(MaterialTheme.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is AllFlightsUiState.Success -> {
                AllFlightsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    data = uiState.data,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick,
                    onBookNowClick = onBookNowClick
                )
            }
        }
    }
}

@Composable
private fun AllFlightsContent(
    modifier: Modifier = Modifier,
    data: AllFlightsData,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBookNowClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.md)
            .padding(top = MaterialTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.all_flights_back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxxl)) // To balance the back button
        }

        SearchCriteriaCard(
            fields = data.fields,
            onSearchClick = onSearchClick
        )

        FlightResultCard(
            card = data.flightCard,
            onBookNowClick = onBookNowClick
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
    }
}

@Composable
private fun SearchCriteriaCard(
    fields: List<FlightFieldUi>,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    FlightFieldRow(
                        modifier = Modifier.fillMaxWidth(),
                        field = fields[0]
                    )
                    FlightFieldRow(
                        modifier = Modifier.fillMaxWidth(),
                        field = fields[1]
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = MaterialTheme.spacing.md)
                        .size(MaterialTheme.spacing.xl),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = MaterialTheme.elevation.md
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(MaterialTheme.spacing.xxl)) {
                        Icon(
                            imageVector = Icons.Filled.SwapVert,
                            contentDescription = stringResource(R.string.all_flights_swap_airports),
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(MaterialTheme.spacing.lg)
                        )
                    }
                }
            }

            FlightFieldRow(
                modifier = Modifier.fillMaxWidth(),
                field = fields[2]
            )
            FlightFieldRow(
                modifier = Modifier.fillMaxWidth(),
                field = fields[3]
            )

            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.all_flights_search_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FlightFieldRow(
    modifier: Modifier = Modifier,
    field: FlightFieldUi
) {
    OutlinedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            width = MaterialTheme.elevation.xs,
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(
                    imageVector = field.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.md)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
            Text(
                text = field.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FlightResultCard(
    card: FlightCardUi,
    onBookNowClick: () -> Unit
) {
    val tealColor = MaterialTheme.colorScheme.primaryContainer
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.spacing.xxl)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(MaterialTheme.spacing.md)
                        )
                    }
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                    Column {
                        Text(
                            text = card.airline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = card.flightCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = card.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.xs,
                            vertical = MaterialTheme.spacing.xxs
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlightTimeBlock(
                    time = card.departureTime,
                    code = card.departureCode,
                    city = card.departureCity
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Surface(
                            modifier = Modifier.size(MaterialTheme.spacing.lg),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(MaterialTheme.spacing.sm)
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Text(
                        text = stringResource(R.string.all_flights_non_stop),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )
                }

                FlightTimeBlock(
                    time = card.arrivalTime,
                    code = card.arrivalCode,
                    city = card.arrivalCity,
                    alignment = Alignment.End
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Duration: ${card.duration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Total (One Way)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.price,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                    Button(
                        onClick = onBookNowClick,
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.height(MaterialTheme.spacing.xxl)
                    ) {
                        Text(
                            text = stringResource(R.string.all_flights_book_now),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlightTimeBlock(
    time: String,
    code: String,
    city: String,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = time,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = code,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = city,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun BottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val items = listOf(
        Triple(BottomNavTab.Explore, Icons.Filled.Home, "Explore"),
        Triple(BottomNavTab.Favorite, Icons.Filled.Favorite, "Favorite"),
        Triple(BottomNavTab.Community, Icons.Filled.Groups, "Community"),
        Triple(BottomNavTab.ChatAi, Icons.Filled.ChatBubble, "Chat AI"),
        Triple(BottomNavTab.Profile, Icons.Filled.Person, "Profile")
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = MaterialTheme.elevation.xxl
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.sm),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (tab, icon, label) ->
                val selected = selectedTab == tab
                val color = if (selected) accentColor else MaterialTheme.colorScheme.outline
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = MaterialTheme.spacing.xxs)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(MaterialTheme.spacing.lg)
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun sampleAllFlightsData() =
    AllFlightsData(
        title = "All Flights",
        fields = listOf(
            FlightFieldUi(Icons.Filled.FlightTakeoff, "Jaipur (JAI), Pakistan"),
            FlightFieldUi(Icons.Filled.FlightLand, "Dubai (DXB), United Arab Emirates"),
            FlightFieldUi(Icons.Filled.CalendarMonth, "March 9, 2024 - 12:00 PM"),
            FlightFieldUi(Icons.Filled.Groups, "1 Passenger, Economy")
        ),
        flightCard = FlightCardUi(
            airline = "Emirates",
            flightCode = "EK015",
            departureTime = "14:30",
            departureCode = "DXB",
            departureCity = "Dubai",
            duration = "7h 15m",
            arrivalTime = "19:45",
            arrivalCode = "LHR",
            arrivalCity = "London",
            price = "$650",
            status = "On Time"
        )
    )

@Preview(showBackground = true)
@Composable
private fun AllFlightsScreenPreview() {
    TravioTheme {
        AllFlightsScreen()
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AllFlightsScreenDarkPreview() {
    TravioTheme(darkTheme = true) {
        AllFlightsScreen()
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun AllFlightsScreenLoadingPreview() {
    TravioTheme {
        AllFlightsScreen(uiState = AllFlightsUiState.Loading)
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun AllFlightsScreenErrorPreview() {
    TravioTheme {
        AllFlightsScreen(uiState = AllFlightsUiState.Error("Unable to load flights"))
    }
}
