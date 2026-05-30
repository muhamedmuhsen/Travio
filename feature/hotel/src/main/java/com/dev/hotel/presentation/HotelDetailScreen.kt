package com.dev.hotel.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.hotel.components.BookingCard
import com.dev.hotel.components.BottomBookingBar
import com.dev.hotel.components.ContactSection
import com.dev.hotel.components.FacilitiesSection
import com.dev.hotel.components.HotelDescriptionSection
import com.dev.hotel.components.HotelDetailShimmer
import com.dev.hotel.components.HotelGallery
import com.dev.hotel.components.HotelInfoHeader
import com.dev.hotel.components.LocationSection
import com.dev.hotel.components.PopularNearbySection
import com.dev.hotel.components.ReviewsSection
import com.dev.hotel.components.RoomCard
import com.dev.utils.uistate.UiState
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelDetails
import com.example.feature.hotel.R
import java.time.format.DateTimeFormatter

@Composable
fun HotelDetailScreen(
    viewModel: HotelDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onBookRoom: (
        rateKey: String,
        hotelCode: Int,
        checkIn: String,
        checkOut: String,
        adults: Int,
        children: Int,
        childrenAges: String?
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                HotelDetailEvent.NavigateBack -> onBackClick()
                HotelDetailEvent.NavigateToReviews -> {
                    snackbarHostState.showSnackbar("Reviews screen coming soon")
                }
                is HotelDetailEvent.NavigateToNearbyDetails -> {
                    snackbarHostState.showSnackbar("Exploring ${event.name} coming soon")
                }
                is HotelDetailEvent.NavigateToBooking -> {
                    val hotelData = (uiState.hotelState as? UiState.Success)?.data
                    val actualRateKey = event.rateKey ?: hotelData?.rooms?.firstOrNull()?.rates?.firstOrNull()?.rateKey
                    if (actualRateKey != null && hotelData != null) {
                        onBookRoom(
                            actualRateKey,
                            hotelData.code,
                            uiState.checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            uiState.checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            uiState.adults,
                            uiState.children,
                            if (uiState.children > 0) uiState.childrenAges.joinToString(",") else null
                        )
                    } else {
                        snackbarHostState.showSnackbar("No available rates for booking")
                    }
                }
                is HotelDetailEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    HotelDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@Composable
fun HotelDetailScreen(
    uiState: HotelDetailUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (HotelDetailAction) -> Unit
) {
    val hotelData = (uiState.hotelState as? UiState.Success)?.data

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        bottomBar = {
            if (hotelData != null && hotelData.minRate != null) {
                BottomBookingBar(
                    price = hotelData.minRate!!,
                    onBookNowClick = { onAction(HotelDetailAction.BookNowClicked) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState.hotelState) {
                is UiState.Idle,
                is UiState.Loading -> {
                    HotelDetailShimmer(modifier = Modifier.align(Alignment.TopCenter))
                }
                is UiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(MaterialTheme.spacing.md)
                    ) {
                        Text(
                            text = stringResource(R.string.hotel_details_error_loading),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                        Button(onClick = { onAction(HotelDetailAction.Retry) }) {
                            Text(text = stringResource(R.string.hotel_details_retry))
                        }
                    }
                }
                is UiState.Success -> {
                    state.data?.let { details ->
                        HotelDetailContent(
                            hotelDetails = details,
                            uiState = uiState,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelDetailContent(
    hotelDetails: HotelDetails,
    uiState: HotelDetailUiState,
    onAction: (HotelDetailAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            HotelGallery(
                images = hotelDetails.images,
                isFavorite = uiState.isFavorite,
                onBackClick = { onAction(HotelDetailAction.BackClicked) },
                onFavoriteClick = { onAction(HotelDetailAction.FavoriteClicked) }
            )
        }
        item {
            HotelInfoHeader(hotelDetails = hotelDetails)
        }

        item {
            BookingCard(
                uiState = uiState,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }

        item {
            HotelDescriptionSection(
                description = hotelDetails.description,
                isExpanded = uiState.isDescriptionExpanded,
                onToggleExpand = {
                    val action = if (uiState.isDescriptionExpanded) {
                        HotelDetailAction.CollapseDescription
                    } else {
                        HotelDetailAction.ExpandDescription
                    }
                    onAction(action)
                }
            )
        }

        item {
            FacilitiesSection(facilities = hotelDetails.facilities)
        }

        item {
            ReviewsSection(onShowAllReviews = { onAction(HotelDetailAction.LoadReviews) })
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }

        item {
            PopularNearbySection(
                nearbyHotelsState = uiState.nearbyHotelsState,
                onExploreClick = { onAction(HotelDetailAction.NearbyExploreClicked(it)) }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }

        if (hotelDetails.rooms.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.hotel_details_rooms),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md)
                )
            }

            items(
                count = hotelDetails.rooms.size,
                key = { index -> hotelDetails.rooms[index].code }
            ) { index ->
                val room = hotelDetails.rooms[index]
                RoomCard(
                    room = room,
                    isExpanded = uiState.expandedRoomCodes.contains(room.code),
                    onToggleExpand = { onAction(HotelDetailAction.ToggleRoomExpansion(room.code)) },
                    onBookRate = { rateKey -> onAction(HotelDetailAction.BookRoom(rateKey)) }
                )
            }
        }

        item {
            LocationSection(hotelDetails = hotelDetails)
        }

        item {
            ContactSection(hotelDetails = hotelDetails)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailScreenLoadingPreview() {
    MaterialTheme {
        HotelDetailScreen(
            uiState = HotelDetailUiState(hotelState = UiState.Loading),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailScreenErrorPreview() {
    MaterialTheme {
        HotelDetailScreen(
            uiState = HotelDetailUiState(hotelState = UiState.Error(com.dev.utils.uitext.UiText.DynamicString("Error"))),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailScreenSuccessPreview() {
    MaterialTheme {
        HotelDetailScreen(
            uiState = HotelDetailUiState(
                hotelState = UiState.Success(
                    HotelDetails(
                        code = 1,
                        name = "Sample Luxury Hotel",
                        description = "This is a sample description of a luxury hotel.",
                        categoryName = "5 Stars",
                        accommodationType = "Hotel",
                        address = "123 Sample St",
                        city = "Sample City",
                        countryCode = "US",
                        latitude = 40.7128,
                        longitude = -74.0060,
                        email = "contact@samplehotel.com",
                        web = "www.samplehotel.com",
                        phones = emptyList(),
                        images = emptyList(),
                        facilities = emptyList(),
                        rooms = emptyList(),
                        minRate = 250.0,
                        maxRate = 500.0,
                        currency = "USD"
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}
