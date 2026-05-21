package com.dev.hotel.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designsystem.icon.getIcon
import com.example.domain.model.hotel.Amenity
import com.example.domain.model.hotel.HotelFacility
import com.example.feature.hotel.R

@Composable
fun FacilitiesSection(
    facilities: List<HotelFacility>,
    modifier: Modifier = Modifier
) {
    if (facilities.isEmpty()) return

    // Filter out facilities that don't have a valid amenity mapping or have duplicate display names
    val uniqueFacilities = facilities
        .map { getFacilityData(it) }
        .filter { it.first != Icons.Default.CheckCircle } // Filter out generic icons
        .distinctBy { it.second } // Filter out duplicate names
        .take(8)

    if (uniqueFacilities.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.hotel_details_facilities),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Standard grid layout to avoid FlowRow binary compatibility issues (NoSuchMethodError)
            val rowCount = (uniqueFacilities.size + 3) / 4
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
                            if (index < uniqueFacilities.size) {
                                val data = uniqueFacilities[index]
                                FacilityItem(
                                    icon = data.first,
                                    name = data.second,
                                    // Fixed width for consistent grid
                                    modifier = Modifier.width(72.dp)
                                )
                            } else {
                                // Empty space for grid alignment
                                Spacer(modifier = Modifier.width(72.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FacilityItem(
    icon: ImageVector,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp) // Adjusted for container internal space
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0F2F1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color(0xFF008080),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getFacilityData(facility: HotelFacility): Pair<ImageVector, String> {
    val amenity = Amenity.fromCode(facility.code)
    return if (amenity != null) {
        // Use a shorter name if available or clean up common prefixes
        val cleanName = amenity.displayName
            .replace(" facilities", "", ignoreCase = true)
            .replace(" service", "", ignoreCase = true)
            .replace(" centre", "", ignoreCase = true)
            .replace("access", "", ignoreCase = true)
            .trim()
            .replaceFirstChar { it.uppercase() }

        Pair(amenity.getIcon(), cleanName)
    } else {
        Pair(Icons.Default.CheckCircle, facility.description ?: "Facility")
    }
}
