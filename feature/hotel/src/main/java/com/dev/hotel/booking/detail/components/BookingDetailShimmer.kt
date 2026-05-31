package com.dev.hotel.booking.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.shimmerEffect
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing

@Composable
fun BookingDetailShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(MaterialTheme.spacing.md)) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(0.6f)
                .clip(MaterialTheme.shapes.small)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.md)
                ) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth(0.4f)
                            .clip(MaterialTheme.shapes.small)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.xxs)
                                .clip(MaterialTheme.shapes.small)
                                .shimmerEffect()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        }
    }
}
