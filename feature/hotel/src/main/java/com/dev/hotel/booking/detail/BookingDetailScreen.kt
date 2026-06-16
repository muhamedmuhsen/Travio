package com.dev.hotel.booking.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.hotel.booking.detail.components.BookingDetailRow
import com.dev.hotel.booking.detail.components.BookingDetailSection
import com.dev.hotel.booking.detail.components.BookingDetailShimmer
import com.dev.hotel.booking.detail.components.CancelBookingDialog
import com.dev.hotel.booking.list.components.BookingStatusChip
import com.example.common.extensions.formatCurrency
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.booking.BookingStatus
import com.example.feature.hotel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onBookingCancelled: () -> Unit = {},
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is BookingDetailEvent.ShowError -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                }
                is BookingDetailEvent.CancelSuccess -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                    onBookingCancelled()
                }
                BookingDetailEvent.NavigateToLogin -> {
                    Toast.makeText(context, context.getString(R.string.session_expired_login), Toast.LENGTH_LONG).show()
                    onNavigateToLogin()
                }
            }
        }
    }

    if (showCancelDialog) {
        CancelBookingDialog(
            onDismissRequest = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                viewModel.cancelBooking()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.booking_info)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            when (val state = uiState) {
                is BookingDetailUiState.Loading -> {
                    BookingDetailShimmer()
                }
                is BookingDetailUiState.Success -> {
                    val details = state.details
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(MaterialTheme.spacing.md)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = details.hotel.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            BookingStatusChip(status = details.status)
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                        BookingDetailSection(title = stringResource(id = R.string.booking_info)) {
                            BookingDetailRow(label = stringResource(id = R.string.booking_reference), value = details.reference)
                            BookingDetailRow(label = stringResource(id = R.string.client_reference), value = details.clientReference)
                            BookingDetailRow(label = stringResource(id = R.string.booking_date), value = details.creationDate)
                            if (details.cancellationReference != null) {
                                BookingDetailRow(
                                    label = stringResource(id = R.string.cancellation_reference),
                                    value = details.cancellationReference!!
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                        BookingDetailSection(title = stringResource(id = R.string.hotel_info)) {
                            BookingDetailRow(label = stringResource(id = R.string.hotel_details_check_in), value = details.hotel.checkIn)
                            BookingDetailRow(label = stringResource(id = R.string.hotel_details_check_out), value = details.hotel.checkOut)
                            BookingDetailRow(label = "Room Count", value = details.hotel.roomCount.toString())
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                        BookingDetailSection(title = stringResource(id = R.string.guest_info)) {
                            BookingDetailRow(label = stringResource(id = R.string.holder_name), value = details.holderName)
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

                        BookingDetailSection(title = stringResource(id = R.string.payment_info)) {
                            BookingDetailRow(
                                label = stringResource(id = R.string.hotel_checkout_total_price),
                                value = formatCurrency(details.currency, details.totalPrice),
                                isBold = true
                            )
                        }

                        if (
                            details.status == BookingStatus.PENDING_PAYMENT ||
                            details.status == BookingStatus.PROCESSING_WEBHOOK ||
                            details.status == BookingStatus.CONFIRMED
                        ) {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
                            Button(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isCancelling,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                if (state.isCancelling) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.size(20.dp).padding(end = MaterialTheme.spacing.xs),
                                        strokeWidth = 2.dp
                                    )
                                    Text(text = stringResource(id = R.string.cancelling))
                                } else {
                                    Text(text = stringResource(id = R.string.cancel_booking))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxxl))
                    }
                }
                is BookingDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message.asString(context),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                        Button(onClick = viewModel::loadBookingDetails) {
                            Text(stringResource(id = R.string.hotel_details_retry))
                        }
                    }
                }
            }
        }
    }
}
