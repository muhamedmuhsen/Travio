package com.dev.search.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.search.presentation.flights.FlightSearchAction
import com.dev.search.presentation.flights.FlightSearchEvent
import com.dev.search.presentation.flights.FlightSearchUiState
import com.dev.search.presentation.flights.FlightSearchViewModel
import com.dev.search.presentation.flights.SearchStatus
import com.example.common.extensions.toCurrencySymbol
import com.example.common.extensions.toFlightDuration
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.AppSnackBar
import com.example.designsystem.components.SnackBarType
import com.example.designsystem.components.showAppSnackbar
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.flights.search.FlightOffer
import com.example.domain.model.flights.search.FlightSegment
import com.example.feature.search.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AIRPORTS = listOf(
    "CAI - Cairo", "HBE - Alexandria", "LXR - Luxor", "ASW - Aswan",
    "SSH - Sharm El Sheikh", "HRG - Hurghada", "SSH - Dahab", "RMF - Marsa Alam",
    "SPX - Giza", "SEW - Siwa", "RAK - Marrakech", "CMN - Casablanca",
    "FEZ - Fes", "AGA - Agadir", "TNG - Tangier", "RBA - Rabat",
    "TNG - Chefchaouen", "ESU - Essaouira", "DXB - Dubai", "AUH - Abu Dhabi",
    "SHJ - Sharjah", "AAN - Al Ain", "RUH - Riyadh", "JED - Jeddah",
    "JED - Mecca", "MED - Medina", "ULH - Al-Ula", "HND - Tokyo",
    "ITM - Kyoto", "KIX - Osaka", "ITM - Nara", "CGK - Jakarta",
    "DPS - Bali", "DPS - Ubud", "YIA - Yogyakarta", "LOP - Lombok",
    "FCO - Rome", "VCE - Venice", "MXP - Milan", "FLR - Florence",
    "NAP - Amalfi", "CDG - Paris", "NCE - Nice", "LYS - Lyon",
    "BOD - Bordeaux", "JFK - New York", "LAX - Los Angeles", "MIA - Miami",
    "LAS - Las Vegas", "SFO - San Francisco", "GIG - Rio de Janeiro",
    "GRU - Sao Paulo", "SSA - Salvador", "BSB - Brasilia", "MAO - Manaus",
    "GIG - Buzios", "FLN - Florianopolis", "EZE - Buenos Aires",
    "MDZ - Mendoza", "BRC - Bariloche", "USH - Ushuaia", "COR - Cordoba",
    "IGR - Iguazu Falls", "LIM - Lima", "CUZ - Cusco", "AQP - Arequipa",
    "CUZ - Machu Picchu Village", "IQT - Iquitos", "BOG - Bogota",
    "MDE - Medellin", "CTG - Cartagena", "CLO - Cali", "SMR - Santa Marta",
    "SCL - Santiago", "SCL - Valparaiso", "SCL - Vina del Mar",
    "CJC - San Pedro de Atacama", "PNT - Puerto Natales", "SYD - Sydney",
    "MEL - Melbourne", "BNE - Brisbane", "PER - Perth", "OOL - Gold Coast"
)

private val CABIN_CLASSES = listOf("ECONOMY", "PREMIUM_ECONOMY", "BUSINESS", "FIRST")
private val ADULT_COUNTS = (1..9).map { it.toString() }

@Composable
fun AllFlightsScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onFlightClick: (offerId: String) -> Unit = {},
    navigateToHome: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToCommunity: () -> Unit = {},
    navigateToTrips: () -> Unit = {},
    navigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FlightSearchEvent.NavigateBack -> onBackClick()
                is FlightSearchEvent.NavigateToFlightDetails -> onFlightClick(event.offerId)
                is FlightSearchEvent.ShowSnackbar -> {
                    snackbarHostState.showAppSnackbar(
                        message = event.message.asString(context),
                        type = SnackBarType.ERROR
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AllFlightsScreen(
            modifier = modifier,
            uiState = uiState,
            onAction = viewModel::onAction,
            navigateToHome = navigateToHome,
            navigateToFavorite = navigateToFavorite,
            navigateToCommunity = navigateToCommunity,
            navigateToTrips = navigateToTrips,
            navigateToProfile = navigateToProfile
        )

        AppSnackBar(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun AllFlightsScreen(
    modifier: Modifier = Modifier,
    uiState: FlightSearchUiState,
    onAction: (FlightSearchAction) -> Unit,
    navigateToHome: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToCommunity: () -> Unit = {},
    navigateToTrips: () -> Unit = {},
    navigateToProfile: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                // Since we usually come from Home/Search which is index 0
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navigateToHome()
                        1 -> navigateToFavorite()
                        2 -> navigateToCommunity()
                        3 -> navigateToTrips()
                        4 -> navigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(FlightSearchAction.OnBackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.all_flights_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(R.string.all_flights_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xxxl))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.md),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = MaterialTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                item {
                    SearchCriteriaCard(
                        uiState = uiState,
                        onAction = onAction
                    )
                }

                when (val status = uiState.searchStatus) {
                    is SearchStatus.Idle -> {
                        item {
                            // Show empty prompt or just space
                        }
                    }
                    is SearchStatus.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.xl), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is SearchStatus.Error -> {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(MaterialTheme.spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = status.message.asString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                if (status.isRetryable) {
                                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                                    Button(onClick = { onAction(FlightSearchAction.OnRetrySearch) }) {
                                        Text(text = stringResource(R.string.retry))
                                    }
                                }
                            }
                        }
                    }
                    is SearchStatus.Success -> {
                        if (status.offers.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.xl),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        stringResource(R.string.all_flights_no_results),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(status.offers) { offer ->
                                FlightResultCard(
                                    offer = offer,
                                    onFlightClick = {
                                        onAction(FlightSearchAction.OnFlightClicked(offer.offerId))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightResultCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FlightResultCard(
                offer = FlightOffer(
                    offerId = "1",
                    origin = "CAI",
                    destination = "CMN",
                    originCityName = "Cairo",
                    destinationCityName = "Casablanca",
                    departureTime = "2024-05-01T22:27:00",
                    arrivalTime = "2024-05-02T01:56:00",
                    totalPrice = 152.82,
                    currency = "GBP",
                    stops = 0,
                    totalDuration = "PT7H15M",
                    airlineLogoUrl = null,
                    segments = listOf(
                        FlightSegment(
                            origin = "CAI",
                            originCityName = "Cairo",
                            destination = "CMN",
                            destinationCityName = "Casablanca",
                            departureTime = "2024-05-01T22:27:00",
                            arrivalTime = "2024-05-02T01:56:00",
                            airlineName = "Duffel Airways",
                            flightNumber = "1807",
                            segmentDuration = "PT7H15M",
                            airlineLogoUrl = null
                        )
                    )
                ),
                onFlightClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchCriteriaCard(
    uiState: FlightSearchUiState,
    onAction: (FlightSearchAction) -> Unit
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
            val params = uiState.searchParams

            Box(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    FlightDropdownRow(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Filled.FlightTakeoff,
                        value = params.origin.ifBlank { stringResource(R.string.all_flights_origin_placeholder) },
                        options = AIRPORTS,
                        onValueChange = {
                            val iata = it.substringBefore(" -").trim()
                            onAction(FlightSearchAction.OnOriginChanged(iata))
                        }
                    )
                    FlightDropdownRow(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Filled.FlightLand,
                        value = params.destination.ifBlank { stringResource(R.string.all_flights_destination_placeholder) },
                        options = AIRPORTS,
                        onValueChange = {
                            val iata = it.substringBefore(" -").trim()
                            onAction(FlightSearchAction.OnDestinationChanged(iata))
                        }
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = MaterialTheme.spacing.md)
                        .size(MaterialTheme.spacing.xl)
                        .clickable {
                            val temp = params.origin
                            onAction(FlightSearchAction.OnOriginChanged(params.destination))
                            onAction(FlightSearchAction.OnDestinationChanged(temp))
                        },
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

            var showDatePicker by remember { mutableStateOf(false) }
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= System.currentTimeMillis() - 86400000
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val formattedDate = formatter.format(Date(millis))
                                onAction(FlightSearchAction.OnDepartureDateChanged(formattedDate))
                            }
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.all_flights_ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.all_flights_cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            FlightClickableRow(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.CalendarMonth,
                value = params.departureDate.ifBlank { stringResource(R.string.all_flights_select_date) },
                onClick = { showDatePicker = true }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                FlightDropdownRow(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Groups,
                    value = if (params.adults == 1) {
                        stringResource(
                            R.string.all_flights_passenger,
                            params.adults
                        )
                    } else {
                        stringResource(R.string.all_flights_passengers, params.adults)
                    },
                    options = ADULT_COUNTS,
                    onValueChange = { onAction(FlightSearchAction.OnAdultsChanged(it.toInt())) }
                )
                FlightDropdownRow(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.AirlineSeatReclineExtra,
                    value = params.cabinClass.ifBlank {
                        stringResource(R.string.all_flights_class_placeholder)
                    }.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() },
                    options = CABIN_CLASSES,
                    onValueChange = { onAction(FlightSearchAction.OnCabinClassChanged(it)) }
                )
            }

            // Display validation errors if any
            if (uiState.validationErrors.isNotEmpty()) {
                Text(
                    text = uiState.validationErrors.values.first().asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xs)
                )
            }

            Button(
                onClick = { onAction(FlightSearchAction.OnSearchClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs),
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
private fun FlightDropdownRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val displayValue = if (options === AIRPORTS) {
        options.find { it.startsWith(value) } ?: value
    } else {
        value
    }

    Box(modifier = modifier) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
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
                    .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(MaterialTheme.spacing.xxl)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(MaterialTheme.spacing.md)
                    )
                }
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FlightClickableRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
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
                .padding(horizontal = MaterialTheme.spacing.xs, vertical = MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxl)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.md)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
            Text(
                text = value,
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
    offer: FlightOffer,
    onFlightClick: () -> Unit
) {
    val firstSegment = offer.segments.firstOrNull()
    val lastSegment = offer.segments.lastOrNull()
    if (firstSegment == null || lastSegment == null) return

    val durationText = offer.totalDuration.toFlightDuration()

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.sm),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFlightClick() }
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.spacing.xxl)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(R.drawable.plane_icon2),
                                    contentDescription = null,
                                    modifier = Modifier.size(MaterialTheme.spacing.md),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                    Column {
                        Text(
                            text = firstSegment.airlineName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = firstSegment.flightNumber,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.all_flights_on_time),
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

            // Main Content Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Times Section
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlightTimeBlock(
                        time = firstSegment.departureTime.substringAfter("T").substring(0, 5),
                        code = firstSegment.origin,
                        city = firstSegment.originCityName,
                        modifier = Modifier.width(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.sm)
                    )

                    // Line with Plane Icon
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            Image(
                                painter = painterResource(R.drawable.plane_icon2),
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.spacing.md),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Text(
                            text = if (offer.stops == 0) {
                                stringResource(R.string.all_flights_non_stop)
                            } else if (offer.stops == 1) {
                                stringResource(R.string.all_flights_one_stop)
                            } else {
                                stringResource(R.string.all_flights_multiple_stops, offer.stops)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 10.sp
                        )
                    }

                    FlightTimeBlock(
                        time = lastSegment.arrivalTime.substringAfter("T").substring(0, 5),
                        code = lastSegment.destination,
                        city = lastSegment.destinationCityName,
                        alignment = Alignment.Start,
                        modifier = Modifier.width(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.sm)
                    )
                }

                // Vertical Divider Section with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.md)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Surface(
                            modifier = Modifier.size(MaterialTheme.spacing.lg),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(MaterialTheme.spacing.sm)
                                )
                            }
                        }
                    }
                }

                // Price Section
                Column(
                    modifier = Modifier.width(MaterialTheme.spacing.xxxl * 2),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                ) {
                    Text(
                        text = stringResource(R.string.all_flights_duration_label, durationText),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.all_flights_total_one_way),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    val currencySymbol = offer.currency.toCurrencySymbol()
                    Text(
                        text = "$currencySymbol${offer.totalPrice}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                    Button(
                        onClick = onFlightClick,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.xxl),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(MaterialTheme.spacing.none)
                    ) {
                        Text(
                            text = stringResource(R.string.all_flights_book_now),
                            style = MaterialTheme.typography.labelMedium,
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
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = alignment, modifier = modifier) {
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
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
