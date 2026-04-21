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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.theme.TravioTheme
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
        containerColor = Color(0xFFF7F9FB),
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
                    tint = Color(0xFF1B1C1E)
                )
            }
            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color(0xFF1B1C1E)
            )
            Spacer(modifier = Modifier.width(48.dp)) // To balance the back button
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .size(32.dp),
                    shape = CircleShape,
                    color = Color.Black,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.SwapVert,
                            contentDescription = stringResource(R.string.all_flights_swap_airports),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
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
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE0E0E0)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = field.icon,
                contentDescription = null,
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
            Text(
                text = field.value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF616161),
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
    val tealColor = Color(0xFF006D77)
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            .size(40.dp)
                            .background(tealColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                    Column {
                        Text(
                            text = card.airline,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1C1E)
                        )
                        Text(
                            text = card.flightCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0F2F1)
                ) {
                    Text(
                        text = card.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = tealColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlightTimeBlock(time = card.departureTime, code = card.departureCode, city = card.departureCity)
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = tealColor
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                    }
                    Text(
                        text = stringResource(R.string.all_flights_non_stop),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E),
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
                        color = Color(0xFF616161),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Total (One Way)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9E9E9E)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.price,
                        style = 24.sp.let { MaterialTheme.typography.headlineSmall.copy(fontSize = it) },
                        color = tealColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                    Button(
                        onClick = onBookNowClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tealColor),
                        modifier = Modifier.height(40.dp)
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
            color = Color(0xFF1B1C1E)
        )
        Text(
            text = code,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1C1E)
        )
        Text(
            text = city,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
private fun BottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val tealColor = Color(0xFF006D77)
    val items = listOf(
        Triple(BottomNavTab.Explore, Icons.Filled.Home, "Explore"),
        Triple(BottomNavTab.Favorite, Icons.Filled.Favorite, "Favorite"),
        Triple(BottomNavTab.Community, Icons.Filled.Groups, "Community"),
        Triple(BottomNavTab.ChatAi, Icons.Filled.ChatBubble, "Chat AI"),
        Triple(BottomNavTab.Profile, Icons.Filled.Person, "Profile")
    )

    Surface(
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (tab, icon, label) ->
                val selected = selectedTab == tab
                val color = if (selected) tealColor else Color(0xFF9E9E9E)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
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
