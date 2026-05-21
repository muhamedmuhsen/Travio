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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.hotel.components.ContactSection
import com.dev.hotel.components.FacilitiesSection
import com.dev.hotel.components.HotelDescriptionSection
import com.dev.hotel.components.HotelDetailShimmer
import com.dev.hotel.components.HotelGallery
import com.dev.hotel.components.HotelInfoHeader
import com.dev.hotel.components.LocationSection
import com.dev.hotel.components.RoomCard
import com.dev.utils.uistate.UiState
import com.example.domain.model.hotel.HotelDetails
import com.example.feature.hotel.R

@Composable
fun HotelDetailScreen(viewModel: HotelDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HotelDetailScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun HotelDetailScreen(
    uiState: HotelDetailUiState,
    onAction: (HotelDetailAction) -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.hotel_details_error_loading),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
            HotelGallery(images = hotelDetails.images)
        }
        item {
            HotelInfoHeader(hotelDetails = hotelDetails)
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

        if (hotelDetails.rooms.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.hotel_details_rooms),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
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
                    onBookRate = { onAction(HotelDetailAction.BookRoom(it)) }
                )
            }
        }

        item {
            LocationSection(hotelDetails = hotelDetails)
        }

        item {
            ContactSection(hotelDetails = hotelDetails)
        }

        // Future sections will be added here
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelDetailScreenLoadingPreview() {
    MaterialTheme {
        HotelDetailScreen(
            uiState = HotelDetailUiState(hotelState = UiState.Loading),
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
            onAction = {}
        )
    }
}
