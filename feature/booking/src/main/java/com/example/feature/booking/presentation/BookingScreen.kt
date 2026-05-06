package com.example.feature.booking.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.AppSnackBar
import com.example.designsystem.components.SnackBarType
import com.example.designsystem.components.showAppSnackbar
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.booking.Passenger
import com.example.feature.booking.R
import com.example.feature.booking.presentation.components.BottomSheetDragHandle
import com.example.feature.booking.presentation.components.PassengerForm
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    offerId: String,
    onBack: () -> Unit,
    onBookingSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> viewModel.onPaymentResult(success = true)
            is PaymentSheetResult.Canceled -> viewModel.onPaymentResult(success = false, canceled = true)
            is PaymentSheetResult.Failed -> viewModel.onPaymentResult(success = false)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookingEffect.ConfirmPayment -> {
                    paymentSheet.presentWithPaymentIntent(effect.clientSecret)
                }
                is BookingEffect.NavigateToConfirmation -> {
                    scope.launch {
                        snackbarHostState.showAppSnackbar(
                            message = context.getString(R.string.booking_payment_success),
                            type = SnackBarType.SUCCESS
                        )
                    }
                    delay(2500)
                    onBookingSuccess(effect.pnr)
                }
                is BookingEffect.ShowError -> {
                    scope.launch {
                        snackbarHostState.showAppSnackbar(
                            message = effect.message,
                            type = SnackBarType.ERROR
                        )
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onBack,
        sheetState = sheetState,
        dragHandle = { BottomSheetDragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        modifier = modifier
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BookingContent(
                uiState = uiState,
                onBack = onBack,
                onPassengerUpdated = viewModel::onPassengerUpdated,
                onAddPassenger = viewModel::onAddPassenger,
                onCheckout = viewModel::onBookNow
            )

            if (uiState.isProcessing) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            AppSnackBar(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun BookingContent(
    uiState: BookingUiState,
    onBack: () -> Unit,
    onPassengerUpdated: (Int, Passenger) -> Unit,
    onAddPassenger: () -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            BookingTopBar(onBack = onBack)
        },
        bottomBar = {
            BookingBottomBar(
                totalPrice = uiState.totalPrice,
                isProcessing = uiState.isProcessing,
                onCheckout = onCheckout
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(uiState.passengers) { index, passenger ->
                    PassengerForm(
                        index = index,
                        passenger = passenger,
                        onPassengerUpdated = { updated ->
                            onPassengerUpdated(index, updated)
                        },
                        errors = uiState.validationErrors[index] ?: emptyList()
                    )
                }

                item {
                    com.example.designsystem.components.AppButton(
                        onClick = onAddPassenger,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.lg),
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        text = stringResource(id = R.string.booking_add_passenger),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingTopBar(onBack: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = com.example.designsystem.R.string.close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.lg)
                )
            }
            Text(
                text = stringResource(id = R.string.booking_passenger_info),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        HorizontalDivider(
            thickness = MaterialTheme.elevation.xs,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun BookingBottomBar(
    totalPrice: String,
    isProcessing: Boolean,
    onCheckout: () -> Unit
) {
    Surface(
        shadowElevation = MaterialTheme.elevation.sm,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.booking_total_price),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = totalPrice,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(MaterialTheme.spacing.md)
                        )
                        Text(
                            text = stringResource(id = R.string.booking_secure_ssl),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                com.example.designsystem.components.AppButton(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs),
                    isEnabled = !isProcessing,
                    shape = MaterialTheme.shapes.medium,
                    text = if (isProcessing) {
                        stringResource(
                            id = R.string.booking_processing
                        )
                    } else {
                        stringResource(id = R.string.booking_checkout)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    MaterialTheme {
        BookingContent(
            uiState = BookingUiState(
                passengers = listOf(
                    Passenger("Mr.", "Jonathan", "Doe", "1990-01-01", "john.doe@example.com", "123456789", "Male")
                ),
                totalPrice = "$1,248.50"
            ),
            onBack = {},
            onPassengerUpdated = { _, _ -> },
            onAddPassenger = {},
            onCheckout = {}
        )
    }
}
