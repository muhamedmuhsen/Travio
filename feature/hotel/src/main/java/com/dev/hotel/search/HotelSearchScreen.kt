package com.dev.hotel.search

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.hotel.components.GuestSelectorSheet
import com.dev.hotel.components.GuestSummaryCard
import com.dev.hotel.components.HotelSearchResultItem
import com.dev.hotel.components.LoadingHotelSearchResultItem
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppTextField
import com.example.designsystem.theme.TravioTheme
import com.example.feature.hotel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HotelSearchScreen(
    onNavigateToDetails: (
        hotelCode: Int,
        checkIn: LocalDate,
        checkOut: LocalDate,
        adults: Int,
        children: Int,
        childrenAges: String?
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HotelSearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HotelSearchEvent.NavigateToHotelDetails -> {
                    onNavigateToDetails(
                        event.hotelCode,
                        event.checkIn,
                        event.checkOut,
                        event.adults,
                        event.children,
                        event.childrenAges
                    )
                }
                HotelSearchEvent.NavigateBack -> onBack()
                is HotelSearchEvent.ShowValidationError -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    HotelSearchScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreenContent(
    state: HotelSearchUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (HotelSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf<DatePickerType?>(null) }

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.hotel_search_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(HotelSearchAction.BackClicked) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Destination Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.hotel_search_destination_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AppTextField(
                        value = state.destination,
                        onValueChange = { onAction(HotelSearchAction.OnDestinationChanged(it)) },
                        placeholder = stringResource(R.string.hotel_search_destination_placeholder),
                        isError = state.destinationError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.destinationError != null) {
                        Text(
                            text = state.destinationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                // Date Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Check-In Date
                    DatePickerField(
                        label = stringResource(R.string.hotel_search_check_in_label),
                        date = state.checkInDate,
                        onClick = { showDatePicker = DatePickerType.CHECK_IN },
                        modifier = Modifier.weight(1f)
                    )

                    // Check-Out Date
                    DatePickerField(
                        label = stringResource(R.string.hotel_search_check_out_label),
                        date = state.checkOutDate,
                        onClick = { showDatePicker = DatePickerType.CHECK_OUT },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (state.dateError != null) {
                    Text(
                        text = state.dateError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Guest Selector Card
                GuestSummaryCard(
                    occupancy = state.occupancies.firstOrNull() ?: com.example.domain.model.hotel.Occupancy(2, 0, emptyList()),
                    onClick = { onAction(HotelSearchAction.OnGuestSheetVisibilityChanged(true)) }
                )

                // Search Button
                Button(
                    onClick = { onAction(HotelSearchAction.SearchClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.hotel_search_button),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Results Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (val resultState = state.searchState) {
                    UiState.Idle -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hotel,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Start searching for hotels",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    UiState.Loading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(4) {
                                LoadingHotelSearchResultItem()
                            }
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.hotel_search_error_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resultState.message.asString(LocalContext.current),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onAction(HotelSearchAction.RetryClicked) },
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(text = "Retry")
                            }
                        }
                    }
                    is UiState.Success -> {
                        val hotels = resultState.data.orEmpty()
                        if (hotels.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.hotel_search_no_results),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.hotel_search_no_results_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(hotels, key = { it.code }) { hotel ->
                                    HotelSearchResultItem(
                                        hotel = hotel,
                                        onClick = { onAction(HotelSearchAction.HotelClicked(hotel.code)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Guest selection
    if (state.isGuestSheetVisible) {
        val firstOccupancy = state.occupancies.firstOrNull() ?: com.example.domain.model.hotel.Occupancy(2, 0, emptyList())
        GuestSelectorSheet(
            occupancy = firstOccupancy,
            onOccupancyChanged = { newOccupancy ->
                onAction(HotelSearchAction.OnOccupancyChanged(listOf(newOccupancy)))
            },
            onDismiss = { onAction(HotelSearchAction.OnGuestSheetVisibilityChanged(false)) }
        )
    }

    // Date Picker Dialog
    if (showDatePicker != null) {
        val initialDate = if (showDatePicker == DatePickerType.CHECK_IN) {
            state.checkInDate ?: LocalDate.now()
        } else {
            state.checkOutDate ?: LocalDate.now().plusDays(1)
        }
        val minDate = if (showDatePicker == DatePickerType.CHECK_OUT) {
            (state.checkInDate ?: LocalDate.now()).plusDays(1)
        } else {
            LocalDate.now()
        }

        DatePickerHelperDialog(
            type = showDatePicker!!,
            initialDate = initialDate,
            minDate = minDate,
            onDateSelected = { date ->
                if (showDatePicker == DatePickerType.CHECK_IN) {
                    onAction(HotelSearchAction.OnCheckInSelected(date))
                } else {
                    onAction(HotelSearchAction.OnCheckOutSelected(date))
                }
                showDatePicker = null
            },
            onDismiss = { showDatePicker = null }
        )
    }
}

@Composable
private fun DatePickerField(
    label: String,
    date: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    val displayText = date?.format(formatter) ?: "Select Date"

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (date != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerHelperDialog(
    type: DatePickerType,
    initialDate: LocalDate,
    minDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                },
                modifier = Modifier.size(height = 48.dp, width = 80.dp)
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.size(height = 48.dp, width = 80.dp)
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = if (type == DatePickerType.CHECK_IN) "Select Check-in Date" else "Select Check-out Date",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

private enum class DatePickerType { CHECK_IN, CHECK_OUT }

@Preview(showBackground = true)
@Composable
fun HotelSearchScreenIdlePreview() {
    TravioTheme {
        HotelSearchScreenContent(
            state = HotelSearchUiState(
                searchState = UiState.Idle,
                destination = "",
                checkInDate = LocalDate.now(),
                checkOutDate = LocalDate.now().plusDays(2),
                occupancies = listOf(com.example.domain.model.hotel.Occupancy(2, 0, emptyList())),
                isGuestSheetVisible = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HotelSearchScreenLoadingPreview() {
    TravioTheme {
        HotelSearchScreenContent(
            state = HotelSearchUiState(
                searchState = UiState.Loading,
                destination = "London",
                checkInDate = LocalDate.now(),
                checkOutDate = LocalDate.now().plusDays(2),
                occupancies = listOf(com.example.domain.model.hotel.Occupancy(2, 0, emptyList())),
                isGuestSheetVisible = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {}
        )
    }
}
