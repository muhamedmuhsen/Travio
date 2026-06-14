package com.example.feature.booking.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.booking.Passenger
import com.example.domain.utils.booking.PassengerValidationError
import com.example.feature.booking.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerForm(
    index: Int,
    passenger: Passenger,
    onPassengerUpdated: (Passenger) -> Unit,
    modifier: Modifier = Modifier,
    errors: List<PassengerValidationError> = emptyList()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateString = formatter.format(Date(millis))
                            onPassengerUpdated(passenger.copy(bornOn = dateString))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        // Lead Passenger Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAddAlt1,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.lg)
                )
            }
            Column {
                Text(
                    text = if (index == 0) {
                        stringResource(
                            id = R.string.booking_lead_passenger
                        )
                    } else {
                        stringResource(id = R.string.booking_passenger_n, index + 1)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.booking_id_details_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TitleSelector(
            selectedTitle = passenger.title,
            onTitleSelected = { onPassengerUpdated(passenger.copy(title = it)) },
            isError = errors.any { it is PassengerValidationError.TitleRequired }
        )

        StyledTextField(
            value = passenger.givenName,
            onValueChange = { onPassengerUpdated(passenger.copy(givenName = it)) },
            label = stringResource(id = R.string.booking_first_name),
            hint = stringResource(id = R.string.booking_first_name_hint),
            isError = errors.any { it is PassengerValidationError.FirstNameRequired },
            supportingText = if (errors.any { it is PassengerValidationError.FirstNameRequired }) {
                stringResource(id = R.string.error_first_name_required)
            } else {
                null
            }
        )

        StyledTextField(
            value = passenger.familyName,
            onValueChange = { onPassengerUpdated(passenger.copy(familyName = it)) },
            label = stringResource(id = R.string.booking_last_name),
            hint = stringResource(id = R.string.booking_last_name_hint),
            isError = errors.any { it is PassengerValidationError.LastNameRequired },
            supportingText = if (errors.any { it is PassengerValidationError.LastNameRequired }) {
                stringResource(id = R.string.error_last_name_required)
            } else {
                null
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Box(modifier = Modifier.weight(1f).clickable { showDatePicker = true }) {
                val dobError = errors.find {
                    it is PassengerValidationError.DateOfBirthRequired || it is PassengerValidationError.InvalidDateFormat
                }
                StyledTextField(
                    value = passenger.bornOn,
                    onValueChange = { },
                    label = stringResource(id = R.string.booking_date_of_birth),
                    hint = stringResource(id = R.string.booking_date_of_birth_hint),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    isError = dobError != null,
                    supportingText = when (dobError) {
                        PassengerValidationError.DateOfBirthRequired -> stringResource(id = R.string.error_dob_required)
                        PassengerValidationError.InvalidDateFormat -> stringResource(id = R.string.error_invalid_date)
                        else -> null
                    }
                )
            }
            GenderSelector(
                selectedGender = passenger.gender,
                onGenderSelected = { onPassengerUpdated(passenger.copy(gender = it)) },
                modifier = Modifier.weight(1f),
                isError = errors.any { it is PassengerValidationError.GenderRequired }
            )
        }

        val phoneError = errors.find {
            it is PassengerValidationError.PhoneRequired || it is PassengerValidationError.InvalidPhoneNumber
        }
        StyledTextField(
            value = passenger.phoneNumber,
            onValueChange = { onPassengerUpdated(passenger.copy(phoneNumber = it)) },
            label = stringResource(id = R.string.booking_phone_number),
            hint = stringResource(id = R.string.booking_phone_number_hint),
            isError = phoneError != null,
            supportingText = when (phoneError) {
                PassengerValidationError.PhoneRequired -> stringResource(id = R.string.error_phone_required)
                PassengerValidationError.InvalidPhoneNumber -> stringResource(id = R.string.error_invalid_phone)
                else -> null
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.xs),
            thickness = MaterialTheme.elevation.xs,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        val emailError = errors.find {
            it is PassengerValidationError.EmailRequired || it is PassengerValidationError.InvalidEmail
        }
        StyledTextField(
            value = passenger.email,
            onValueChange = { onPassengerUpdated(passenger.copy(email = it)) },
            label = stringResource(id = R.string.booking_email),
            hint = stringResource(id = R.string.booking_email_hint),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = if (emailError != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(MaterialTheme.spacing.lg)
                )
            },
            isError = emailError != null,
            supportingText = when (emailError) {
                PassengerValidationError.EmailRequired -> stringResource(id = R.string.error_email_required)
                PassengerValidationError.InvalidEmail -> stringResource(id = R.string.error_invalid_email)
                else -> stringResource(id = R.string.booking_email_note)
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
    }
}

@Preview(showBackground = true)
@Composable
fun PassengerFormPreview() {
    MaterialTheme {
        PassengerForm(
            index = 0,
            passenger = Passenger("id123", "Mr.", "Jonathan", "Doe", "1990-01-01", "john.doe@example.com", "123456789", "Male"),
            onPassengerUpdated = {}
        )
    }
}
