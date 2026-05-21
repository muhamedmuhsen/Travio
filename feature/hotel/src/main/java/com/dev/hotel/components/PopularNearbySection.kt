package com.dev.hotel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.DestinationCard
import com.example.designsystem.components.LoadingDestinationCard
import com.example.domain.model.hotel.NearbyHotel
import com.example.feature.hotel.R

@Composable
fun PopularNearbySection(
    nearbyHotelsState: UiState<List<NearbyHotel>>,
    onExploreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (nearbyHotelsState is UiState.Error || nearbyHotelsState is UiState.Idle) {
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.hotel_details_popular_nearby),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (nearbyHotelsState) {
                is UiState.Loading -> {
                    items(3) {
                        LoadingDestinationCard()
                    }
                }
                is UiState.Success -> {
                    val hotels = nearbyHotelsState.data ?: emptyList()
                    items(
                        items = hotels,
                        key = { it.code }
                    ) { hotel ->
                        DestinationCard(
                            title = hotel.name,
                            rating = 0.0,
                            reviewCount = 0,
                            description = hotel.categoryName ?: hotel.destinationName ?: "",
                            price = "${hotel.currency ?: "$"} ${hotel.minRate ?: 0.0}",
                            imageUrl = hotel.thumbnailImage ?: "",
                            isFavorite = false,
                            onFavoriteClicked = { },
                            onCardClicked = { onExploreClick(hotel.name) }
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}
