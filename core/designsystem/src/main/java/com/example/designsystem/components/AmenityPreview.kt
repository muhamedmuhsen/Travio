package com.example.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.icon.getIcon
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.Amenity

@Composable
fun AmenityItem(
    amenity: Amenity,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = amenity.getIcon(),
            contentDescription = amenity.displayName,
            modifier = Modifier.size(MaterialTheme.spacing.lg),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
        Column {
            Text(
                text = amenity.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Code: ${amenity.code}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AmenitiesPreview() {
    TravioTheme {
        Surface {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = MaterialTheme.spacing.xxxl * 3 + MaterialTheme.spacing.xs),
                modifier = Modifier.padding(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                items(Amenity.entries) { amenity ->
                    AmenityItem(amenity = amenity)
                }
            }
        }
    }
}
