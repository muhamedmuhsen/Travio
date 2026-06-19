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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.BottomSheetDragHandle
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.Occupancy
import com.example.feature.hotel.R

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
        shape = RoundedCornerShape(topStart = MaterialTheme.spacing.lg, topEnd = MaterialTheme.spacing.lg),
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
                text = stringResource(R.string.hotel_search_select_guests),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            // Adults selector (1 - 6)
            CounterRow(
                label = stringResource(R.string.hotel_search_adults),
                description = stringResource(R.string.hotel_search_adults_desc),
                count = occupancy.adults,
                minCount = 1,
                maxCount = 6,
                onCountChanged = { adultsCount ->
                    onOccupancyChanged(occupancy.copy(adults = adultsCount))
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            // Children selector (0 - 4)
            CounterRow(
                label = stringResource(R.string.hotel_search_children),
                description = stringResource(R.string.hotel_search_children_desc),
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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
                Text(
                    text = stringResource(R.string.hotel_search_children_ages_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

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
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))

            AppButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.hotel_search_apply),
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
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            IconButton(
                onClick = { if (count > minCount) onCountChanged(count - 1) },
                enabled = count > minCount
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(id = com.example.designsystem.R.string.close))
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(MaterialTheme.spacing.lg),
                onTextLayout = {}
            )
            IconButton(
                onClick = { if (count < maxCount) onCountChanged(count + 1) },
                enabled = count < maxCount
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.increase))
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
            text = stringResource(R.string.hotel_search_child_age_label, index + 1),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            IconButton(
                onClick = { if (age > 0) onAgeChanged(age - 1) },
                enabled = age > 0
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(R.string.decrease_age))
            }
            Text(
                text = if (age < 1) {
                    stringResource(
                        R.string.hotel_search_child_under_one
                    )
                } else {
                    stringResource(R.string.hotel_search_child_years, age)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(MaterialTheme.spacing.xxxl),
                onTextLayout = {}
            )
            IconButton(
                onClick = { if (age < 17) onAgeChanged(age + 1) },
                enabled = age < 17
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.increase_age))
            }
        }
    }
}
