package com.dev.hotel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dev.hotel.presentation.HotelDetailAction
import com.dev.hotel.presentation.HotelDetailUiState
import com.example.domain.model.hotel.Occupancy
import com.example.feature.hotel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookingCard(
    uiState: HotelDetailUiState,
    onAction: (HotelDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGuestSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf<DatePickerType?>(null) }

    val hotelData = (uiState.hotelState as? com.dev.utils.uistate.UiState.Success)?.data
    val price = hotelData?.minRate ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$$price",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.hotel_details_per_night),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                BookingField(
                    label = stringResource(R.string.hotel_details_check_in),
                    value = uiState.checkInDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                    onClick = { showDatePicker = DatePickerType.CHECK_IN },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                BookingField(
                    label = stringResource(R.string.hotel_details_check_out),
                    value = uiState.checkOutDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                    onClick = { showDatePicker = DatePickerType.CHECK_OUT },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Updated Layout: 1st half for Guests (Adults/Children), 2nd half for Children Ages
            Row(modifier = Modifier.fillMaxWidth()) {
                // Guests Field
                BookingField(
                    label = stringResource(R.string.hotel_details_guests),
                    value = "${uiState.adults + uiState.children} Guests",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = { showGuestSheet = true },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Children Ages Field
                val agesText = if (uiState.children > 0) {
                    uiState.childrenAges.joinToString(", ")
                } else {
                    "None"
                }

                BookingField(
                    label = "Children Ages",
                    value = agesText,
                    onClick = { if (uiState.children > 0) showGuestSheet = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAction(HotelDetailAction.CheckAvailabilityClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !uiState.isSearchingRooms
            ) {
                if (uiState.isSearchingRooms) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        text = stringResource(R.string.hotel_details_check_availability),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showGuestSheet) {
        GuestSelectorSheet(
            occupancy = Occupancy(
                adults = uiState.adults,
                children = uiState.children,
                childrenAges = uiState.childrenAges
            ),
            onOccupancyChanged = { occupancy ->
                onAction(HotelDetailAction.OnOccupancyChanged(occupancy))
            },
            onDismiss = { showGuestSheet = false }
        )
    }

    if (showDatePicker != null) {
        HotelDatePickerDialog(
            type = showDatePicker!!,
            initialDate = if (showDatePicker == DatePickerType.CHECK_IN) uiState.checkInDate else uiState.checkOutDate,
            minDate = if (showDatePicker == DatePickerType.CHECK_OUT) uiState.checkInDate.plusDays(1) else LocalDate.now(),
            onDateSelected = { date ->
                if (showDatePicker == DatePickerType.CHECK_IN) {
                    onAction(HotelDetailAction.OnCheckInDateSelected(date))
                } else {
                    onAction(HotelDetailAction.OnCheckOutDateSelected(date))
                }
                showDatePicker = null
            },
            onDismiss = { showDatePicker = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotelDatePickerDialog(
    type: DatePickerType,
    initialDate: LocalDate,
    minDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(date)
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState, title = {
            Text(if (type == DatePickerType.CHECK_IN) "Select Check-in" else "Select Check-out", modifier = Modifier.padding(16.dp))
        })
    }
}

@Composable
private fun BookingField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private enum class DatePickerType { CHECK_IN, CHECK_OUT }
