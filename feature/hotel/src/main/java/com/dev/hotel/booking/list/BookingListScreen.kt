package com.dev.hotel.booking.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.hotel.booking.list.components.BookingCard
import com.dev.hotel.booking.list.components.BookingCardShimmer
import com.dev.hotel.booking.list.components.EmptyBookingsState
import com.example.designsystem.theme.spacing
import com.example.feature.hotel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToHotels: () -> Unit,
    shouldRefresh: Boolean = false,
    viewModel: BookingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadBookings()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingListEvent.NavigateToDetail -> onNavigateToDetail(event.reference, event.totalPrice, event.currency)
                BookingListEvent.NavigateToHotels -> onNavigateToHotels()
                is BookingListEvent.ShowError -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.my_bookings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = com.example.designsystem.R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            when (val state = uiState) {
                is BookingListUiState.Loading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.spacing.md)
                    ) {
                        items(5) {
                            BookingCardShimmer()
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                        }
                    }
                }
                is BookingListUiState.Empty -> {
                    EmptyBookingsState(
                        onExploreHotelsClick = viewModel::onExploreHotelsClicked
                    )
                }
                is BookingListUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.spacing.md)
                    ) {
                        items(
                            items = state.bookings,
                            key = { it.reference }
                        ) { booking ->
                            BookingCard(
                                booking = booking,
                                onClick = { viewModel.onBookingClicked(booking) }
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                        }
                    }
                }
                is BookingListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message.asString(context),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                        Button(onClick = viewModel::loadBookings) {
                            Text(stringResource(id = R.string.hotel_details_retry))
                        }
                    }
                }
            }
        }
    }
}
