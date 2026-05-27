package com.dev.hotel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.BottomSheetDragHandle
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.Occupancy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestSelectorSheet(
    occupancy: Occupancy,
    onOccupancyChanged: (Occupancy) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        modifier = modifier.statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg)
        ) {
            Text(
                text = "Select Guests",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            // Adults selector (1 - 6)
            CounterRow(
                label = "Adults",
                description = "Ages 18 or above",
                count = occupancy.adults,
                minCount = 1,
                maxCount = 6,
                onCountChanged = { adultsCount ->
                    onOccupancyChanged(occupancy.copy(adults = adultsCount))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Children selector (0 - 4)
            CounterRow(
                label = "Children",
                description = "Ages 0 to 17",
                count = occupancy.children,
                minCount = 0,
                maxCount = 4,
                onCountChanged = { childrenCount ->
                    val currentAges = occupancy.childrenAges.toMutableList()
                    if (childrenCount > currentAges.size) {
                        while (currentAges.size < childrenCount) {
                            currentAges.add(8) // Default age
                        }
                    } else if (childrenCount < currentAges.size) {
                        while (currentAges.size > childrenCount) {
                            currentAges.removeAt(currentAges.lastIndex)
                        }
                    }
                    onOccupancyChanged(occupancy.copy(children = childrenCount, childrenAges = currentAges))
                }
            )

            // Children Ages selector
            if (occupancy.children > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Children's Ages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                occupancy.childrenAges.forEachIndexed { index, age ->
                    ChildAgeSelector(
                        index = index,
                        age = age,
                        onAgeChanged = { newAge ->
                            val updatedAges = occupancy.childrenAges.toMutableList()
                            updatedAges[index] = newAge
                            onOccupancyChanged(occupancy.copy(childrenAges = updatedAges))
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

            AppButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                text = "Apply",
                buttonHeight = 56
            )
        }
    }
}

@Composable
private fun CounterRow(
    label: String,
    description: String,
    count: Int,
    minCount: Int,
    maxCount: Int,
    onCountChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (count > minCount) onCountChanged(count - 1) },
                enabled = count > minCount
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp),
                onTextLayout = {}
            )
            IconButton(
                onClick = { if (count < maxCount) onCountChanged(count + 1) },
                enabled = count < maxCount
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
private fun ChildAgeSelector(
    index: Int,
    age: Int,
    onAgeChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Child ${index + 1} Age",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (age > 0) onAgeChanged(age - 1) },
                enabled = age > 0
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease age")
            }
            Text(
                text = "$age yrs",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(48.dp),
                onTextLayout = {}
            )
            IconButton(
                onClick = { if (age < 17) onAgeChanged(age + 1) },
                enabled = age < 17
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase age")
            }
        }
    }
}
