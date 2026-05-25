package com.dev.hotel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dev.hotel.presentation.HotelDetailAction
import com.dev.hotel.presentation.HotelDetailUiState
import com.example.feature.hotel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookingCard(
    uiState: HotelDetailUiState,
    onAction: (HotelDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGuestDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf<DatePickerType?>(null) }

    val hotelData = (uiState.hotelState as? com.dev.utils.uistate.UiState.Success)?.data
    val price = hotelData?.minRate ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
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
                    onClick = { showGuestDialog = true },
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
                    onClick = { if (uiState.children > 0) showGuestDialog = true },
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
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        text = stringResource(R.string.hotel_details_check_availability),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showGuestDialog) {
        GuestSelectionDialog(
            uiState = uiState,
            onAction = onAction,
            onDismiss = { showGuestDialog = false }
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
private fun GuestSelectionDialog(
    uiState: HotelDetailUiState,
    onAction: (HotelDetailAction) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Guests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                CounterRow(label = "Adults", count = uiState.adults, onCountChanged = {
                    onAction(HotelDetailAction.OnAdultsCountChanged(it))
                }, minCount = 1)
                CounterRow(
                    label = "Children",
                    count = uiState.children,
                    onCountChanged = { onAction(HotelDetailAction.OnChildrenCountChanged(it)) }
                )

                if (uiState.children > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Children Ages", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.childrenAges.forEachIndexed { index, age ->
                        AgeSelector(index = index, age = age, onAgeChanged = { onAction(HotelDetailAction.OnChildAgeChanged(index, it)) })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun CounterRow(
    label: String,
    count: Int,
    onCountChanged: (Int) -> Unit,
    minCount: Int = 0
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (count > minCount) onCountChanged(count - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text("$count", modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { onCountChanged(count + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
private fun AgeSelector(
    index: Int,
    age: Int,
    onAgeChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Child ${index + 1} Age")
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (age > 0) onAgeChanged(age - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text("$age", modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { if (age < 17) onAgeChanged(age + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
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
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
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
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
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
