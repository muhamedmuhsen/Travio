package com.dev.hotel.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelRoom
import com.example.domain.model.hotel.RoomRate
import com.example.feature.hotel.R

@Composable
fun RoomCard(
    room: HotelRoom,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onBookRate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Collapsed Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (room.rates.isNotEmpty()) {
                        val minPrice = room.rates.mapNotNull { it.price }.minOrNull()
                        if (minPrice != null) {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                            Text(
                                text = stringResource(R.string.hotel_details_starting_from_price, minPrice),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs)
                ) {
                    if (room.images.isNotEmpty()) {
                        HotelGallery(
                            images = room.images,
                            modifier = Modifier.height(MaterialTheme.spacing.xxxl * 3 + MaterialTheme.spacing.xs)
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                    }

                    if (room.rates.isEmpty()) {
                        Text(
                            text = stringResource(R.string.hotel_details_pricing_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.hotel_details_available_rates),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                        room.rates.forEachIndexed { index, rate ->
                            if (index > 0) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                            RoomRateItem(
                                rate = rate,
                                onBookNowClick = { onBookRate(rate.rateKey) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomCardPreview() {
    TravioTheme {
        RoomCard(
            room = HotelRoom(
                code = "DBL.DX",
                name = "Double Deluxe Room With Garden View",
                images = emptyList(),
                roomFacilities = emptyList(),
                rates = listOf(
                    RoomRate(
                        rateKey = "key1",
                        rateClass = "NOR",
                        price = 121.89,
                        boardCode = "RO",
                        boardName = "Room Only",
                        allotment = 5,
                        cancellationPolicies = emptyList()
                    )
                )
            ),
            isExpanded = false,
            onToggleExpand = {},
            onBookRate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomCardDarkPreview() {
    TravioTheme(darkTheme = true) {
        RoomCard(
            room = HotelRoom(
                code = "DBL.DX",
                name = "Double Deluxe Room With Garden View",
                images = emptyList(),
                roomFacilities = emptyList(),
                rates = listOf(
                    RoomRate(
                        rateKey = "key1",
                        rateClass = "NOR",
                        price = 121.89,
                        boardCode = "RO",
                        boardName = "Room Only",
                        allotment = 5,
                        cancellationPolicies = emptyList()
                    )
                )
            ),
            isExpanded = true,
            onToggleExpand = {},
            onBookRate = {}
        )
    }
}
