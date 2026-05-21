package com.dev.hotel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.hotel.HotelFacility
import com.example.feature.hotel.R

@Composable
fun FacilitiesSection(
    facilities: List<HotelFacility>,
    modifier: Modifier = Modifier
) {
    if (facilities.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.hotel_details_facilities),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        val maxItems = minOf(facilities.size, 8)
        val rowCount = (maxItems + 3) / 4
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (rowIndex in 0 until rowCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (colIndex in 0 until 4) {
                        val index = rowIndex * 4 + colIndex
                        if (index < maxItems) {
                            FacilityItem(facility = facilities[index])
                        } else {
                            Spacer(modifier = Modifier.width(72.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FacilityItem(facility: HotelFacility) {
    val (icon, name) = getFacilityData(facility)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // Fixed width to align grid nicely
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

// Maps standard amenity codes/names to icons
private fun getFacilityData(facility: HotelFacility): Pair<ImageVector, String> {
    val name = facility.description ?: "Facility"
    val lowerName = name.lowercase()

    val icon = when {
        lowerName.contains("wifi") || lowerName.contains("internet") -> Icons.Default.Wifi
        lowerName.contains("pool") -> Icons.Default.Pool
        lowerName.contains("gym") || lowerName.contains("fitness") -> Icons.Default.FitnessCenter
        lowerName.contains("park") -> Icons.Default.DirectionsCar
        lowerName.contains("spa") || lowerName.contains("massage") -> Icons.Default.Spa
        lowerName.contains("room service") -> Icons.Default.RoomService
        lowerName.contains("bar") || lowerName.contains("lounge") -> Icons.Default.LocalBar
        lowerName.contains("dine") || lowerName.contains("restaurant") -> Icons.Default.LocalDining
        lowerName.contains("breakfast") -> Icons.Default.LocalCafe
        else -> Icons.Default.CheckCircle
    }

    return Pair(icon, name)
}
