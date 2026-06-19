package com.dev.hotel.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppButton
import com.example.designsystem.components.AppTextField
import com.example.designsystem.components.BottomSheetDragHandle
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.domain.model.hotel.HotelBookingPax
import com.example.domain.model.hotel.HotelBookingRoom
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.utils.hotel.HotelCheckoutValidationError
import com.example.feature.hotel.R
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun HotelCheckoutScreen(
    viewModel: HotelCheckoutViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: (bookingId: String, hotelName: String, checkIn: String, checkOut: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var currentBookingId by remember { mutableStateOf("") }

    val paymentSheet = rememberPaymentSheet { paymentResult ->
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                viewModel.onAction(HotelCheckoutAction.PaymentCompleted(currentBookingId))
            }
            is PaymentSheetResult.Failed -> {
                viewModel.onAction(
                    HotelCheckoutAction.PaymentFailed(
                        paymentResult.error.localizedMessage ?: context.getString(R.string.hotel_checkout_payment_failed)
                    )
                )
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.onAction(HotelCheckoutAction.PaymentCanceled)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                HotelCheckoutUiEvent.NavigateBack -> onBackClick()
                is HotelCheckoutUiEvent.NavigateToSuccess -> {
                    val hotelName = (uiState.hotelState as? UiState.Success)?.data?.name ?: "Hotel"
                    onSuccess(
                        event.bookingId,
                        hotelName,
                        uiState.checkIn,
                        uiState.checkOut
                    )
                }
                is HotelCheckoutUiEvent.LaunchPaymentSheet -> {
                    currentBookingId = event.bookingId
                    paymentSheet.presentWithPaymentIntent(
                        event.clientSecret,
                        PaymentSheet.Configuration("Travio")
                    )
                }
                is HotelCheckoutUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    HotelCheckoutScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelCheckoutScreen(
    uiState: HotelCheckoutUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(HotelCheckoutAction.BackClicked) },
        sheetState = sheetState,
        dragHandle = { BottomSheetDragHandle() },
        shape = RoundedCornerShape(topStart = MaterialTheme.spacing.lg, topEnd = MaterialTheme.spacing.lg),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        modifier = Modifier.statusBarsPadding()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().imePadding(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                HotelCheckoutTopBar(onBack = { onAction(HotelCheckoutAction.BackClicked) })
            },
            bottomBar = {
                val hotelData = (uiState.hotelState as? UiState.Success)?.data
                val price = uiState.selectedRatePrice ?: hotelData?.minRate
                val currency = uiState.selectedRateCurrency ?: hotelData?.currency ?: "USD"

                if (price != null) {
                    CheckoutBottomBar(
                        price = price,
                        currency = currency,
                        isSubmitting = uiState.isSubmitting || uiState.isPaymentProcessing,
                        onSubmitClick = { onAction(HotelCheckoutAction.SubmitCheckout) }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when (val state = uiState.hotelState) {
                    is UiState.Idle,
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(MaterialTheme.spacing.lg),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.error_icon),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.md)
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                            Text(
                                text = stringResource(R.string.hotel_checkout_error_loading_details),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                            Text(
                                text = state.message.asString(),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
                            Button(
                                onClick = { onAction(HotelCheckoutAction.BackClicked) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(text = stringResource(R.string.hotel_checkout_try_again))
                            }
                        }
                    }
                    is UiState.Success -> {
                        state.data?.let { hotelDetails ->
                            CheckoutContent(
                                hotelDetails = hotelDetails,
                                uiState = uiState,
                                onAction = onAction
                            )
                        }
                    }
                }

                if (uiState.isSubmitting || uiState.isPaymentProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
                            Text(
                                text = stringResource(
                                    if (uiState.isPaymentProcessing) {
                                        R.string.hotel_checkout_processing_payment
                                    } else {
                                        R.string.hotel_checkout_submitting
                                    }
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelCheckoutTopBar(onBack: () -> Unit) {
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
                text = stringResource(id = R.string.hotel_checkout_title),
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
private fun CheckoutContent(
    hotelDetails: HotelDetails,
    uiState: HotelCheckoutUiState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        item { Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs)) }

        // Hotel Info Summary Card
        item {
            HotelCheckoutSummaryCard(hotelDetails = hotelDetails, uiState = uiState)
        }

        // Room Rate / Occupancy summary Card
        item {
            RoomOccupancyCard(hotelDetails = hotelDetails, uiState = uiState)
        }

        // Booking Holder details
        item {
            BookingHolderCard(uiState = uiState, onAction = onAction)
        }

        // Passenger info section per room
        itemsIndexed(uiState.rooms) { rIdx, room ->
            RoomGuestsCard(room = room, roomIndex = rIdx, uiState = uiState, onAction = onAction)
        }

        // Remarks / Special Requests
        item {
            RemarksCard(uiState = uiState, onAction = onAction)
        }

        // Error message if any
        if (uiState.errorMessage != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(id = com.example.designsystem.R.string.error),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl)) }
    }
}

@Composable
private fun HotelCheckoutSummaryCard(
    hotelDetails: HotelDetails,
    uiState: HotelCheckoutUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val thumbnail = hotelDetails.images.firstOrNull()?.url
            AsyncImage(
                model = thumbnail,
                contentDescription = stringResource(R.string.hotel_thumbnail),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xl)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.sm))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotelDetails.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxs))
                Text(
                    text = hotelDetails.address + ", " + hotelDetails.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    DateBlock(
                        label = stringResource(R.string.hotel_details_check_in),
                        date = uiState.checkIn
                    )
                    DateBlock(
                        label = stringResource(R.string.hotel_details_check_out),
                        date = uiState.checkOut
                    )
                }
            }
        }
    }
}

@Composable
private fun DateBlock(
    label: String,
    date: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RoomOccupancyCard(
    hotelDetails: HotelDetails,
    uiState: HotelCheckoutUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CardMembership,
                    contentDescription = stringResource(R.string.room_details),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.hotel_search_guests_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            Text(
                text = stringResource(
                    R.string.hotel_search_guests_summary,
                    uiState.adultsCount,
                    uiState.childrenCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            val roomName = hotelDetails.rooms.firstOrNull()?.name ?: "Standard Room"
            Text(
                text = roomName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BookingHolderCard(
    uiState: HotelCheckoutUiState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.holder_details),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.hotel_checkout_holder_section),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

            val holderFirstNameError = uiState.validationErrors.firstOrNull {
                it == HotelCheckoutValidationError.EmptyHolderFirstName || it == HotelCheckoutValidationError.ShortHolderFirstName
            }
            AppTextField(
                value = uiState.holderFirstName,
                onValueChange = { onAction(HotelCheckoutAction.UpdateHolderFirstName(it)) },
                placeholder = stringResource(R.string.hotel_checkout_first_name_label),
                isError = holderFirstNameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (holderFirstNameError != null) {
                Text(
                    text = holderFirstNameError.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, top = MaterialTheme.spacing.xxs)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            val holderLastNameError = uiState.validationErrors.firstOrNull {
                it == HotelCheckoutValidationError.EmptyHolderLastName || it == HotelCheckoutValidationError.ShortHolderLastName
            }
            AppTextField(
                value = uiState.holderLastName,
                onValueChange = { onAction(HotelCheckoutAction.UpdateHolderLastName(it)) },
                placeholder = stringResource(R.string.hotel_checkout_last_name_label),
                isError = holderLastNameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (holderLastNameError != null) {
                Text(
                    text = holderLastNameError.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, top = MaterialTheme.spacing.xxs)
                )
            }
        }
    }
}

@Composable
private fun RoomGuestsCard(
    room: HotelBookingRoom,
    roomIndex: Int,
    uiState: HotelCheckoutUiState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        ) {
            Text(
                text = stringResource(R.string.hotel_checkout_room_header, roomIndex + 1),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            val roomError = uiState.validationErrors.firstOrNull { error ->
                when (error) {
                    is HotelCheckoutValidationError.NoAdultInRoom -> error.roomIndex == roomIndex
                    is HotelCheckoutValidationError.EmptyRoom -> error.roomIndex == roomIndex
                    else -> false
                }
            }

            if (roomError != null) {
                Text(
                    text = roomError.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs)
                )
            }

            room.paxes.forEachIndexed { pIdx, pax ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.xs)
                ) {
                    val paxTypeLabel = if (pax.type == "AD") {
                        stringResource(
                            R.string.hotel_search_adults
                        )
                    } else {
                        stringResource(R.string.hotel_search_children)
                    }
                    Text(
                        text = stringResource(R.string.hotel_checkout_guest_header, pIdx + 1, paxTypeLabel),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                    val paxNameError = uiState.validationErrors.firstOrNull {
                        it is HotelCheckoutValidationError.EmptyPaxName && it.roomIndex == roomIndex && it.paxIndex == pIdx
                    }
                    AppTextField(
                        value = pax.name,
                        onValueChange = { onAction(HotelCheckoutAction.UpdatePaxName(roomIndex, pIdx, it)) },
                        placeholder = stringResource(R.string.hotel_checkout_first_name_label),
                        isError = paxNameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (paxNameError != null) {
                        Text(
                            text = paxNameError.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, top = MaterialTheme.spacing.xxs)
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))

                    val paxSurnameError = uiState.validationErrors.firstOrNull {
                        it is HotelCheckoutValidationError.EmptyPaxSurname && it.roomIndex == roomIndex && it.paxIndex == pIdx
                    }
                    AppTextField(
                        value = pax.surname,
                        onValueChange = { onAction(HotelCheckoutAction.UpdatePaxSurname(roomIndex, pIdx, it)) },
                        placeholder = stringResource(R.string.hotel_checkout_last_name_label),
                        isError = paxSurnameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (paxSurnameError != null) {
                        Text(
                            text = paxSurnameError.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, top = MaterialTheme.spacing.xxs)
                        )
                    }

                    if (pax.type == "CH") {
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xs))
                        val childAgeError = uiState.validationErrors.firstOrNull { error ->
                            when (error) {
                                is HotelCheckoutValidationError.MissingChildAge -> error.roomIndex == roomIndex && error.paxIndex == pIdx
                                is HotelCheckoutValidationError.InvalidChildAge -> error.roomIndex == roomIndex && error.paxIndex == pIdx
                                else -> false
                            }
                        }
                        AppTextField(
                            value = pax.age?.toString() ?: "",
                            onValueChange = {
                                val ageVal = it.toIntOrNull()
                                onAction(HotelCheckoutAction.UpdatePaxAge(roomIndex, pIdx, ageVal))
                            },
                            placeholder = stringResource(R.string.hotel_search_child_age_label, pIdx + 1),
                            isError = childAgeError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (childAgeError != null) {
                            Text(
                                text = childAgeError.asString(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = MaterialTheme.spacing.xxs, top = MaterialTheme.spacing.xxs)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RemarksCard(
    uiState: HotelCheckoutUiState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.spacing.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.xs)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notes,
                    contentDescription = stringResource(R.string.remarks_icon),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.hotel_checkout_remarks_section),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
            AppTextField(
                value = uiState.remark,
                onValueChange = { onAction(HotelCheckoutAction.UpdateRemark(it)) },
                placeholder = stringResource(R.string.hotel_checkout_remarks_hint),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    price: Double,
    currency: String,
    isSubmitting: Boolean,
    onSubmitClick: () -> Unit
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
                thickness = MaterialTheme.elevation.xs,
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
                            text = stringResource(R.string.hotel_checkout_total_price),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        val symbol = when (currency.uppercase()) {
                            "USD" -> "$"
                            "EUR" -> "€"
                            "GBP" -> "£"
                            else -> "$currency "
                        }
                        Text(
                            text = String.format("%s%.2f", symbol, price),
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
                            text = stringResource(R.string.hotel_checkout_secure_ssl),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                AppButton(
                    onClick = onSubmitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs),
                    isEnabled = !isSubmitting,
                    shape = MaterialTheme.shapes.medium,
                    text = if (isSubmitting) {
                        stringResource(R.string.hotel_checkout_submitting)
                    } else {
                        stringResource(R.string.hotel_checkout_button)
                    }
                )
            }
        }
    }
}

@Composable
fun HotelCheckoutValidationError.asString(): String {
    return when (this) {
        HotelCheckoutValidationError.EmptyHolderFirstName -> stringResource(R.string.hotel_checkout_first_name_required)
        HotelCheckoutValidationError.EmptyHolderLastName -> stringResource(R.string.hotel_checkout_last_name_required)
        HotelCheckoutValidationError.ShortHolderFirstName -> stringResource(R.string.hotel_checkout_first_name_too_short)
        HotelCheckoutValidationError.ShortHolderLastName -> stringResource(R.string.hotel_checkout_last_name_too_short)
        is HotelCheckoutValidationError.EmptyPaxName -> stringResource(R.string.hotel_checkout_guest_first_name_required)
        is HotelCheckoutValidationError.EmptyPaxSurname -> stringResource(R.string.hotel_checkout_guest_last_name_required)
        is HotelCheckoutValidationError.MissingChildAge -> stringResource(R.string.hotel_checkout_child_age_required)
        is HotelCheckoutValidationError.InvalidChildAge -> stringResource(R.string.hotel_checkout_child_age_invalid)
        is HotelCheckoutValidationError.NoAdultInRoom -> stringResource(R.string.hotel_checkout_room_needs_adult)
        is HotelCheckoutValidationError.EmptyRoom -> stringResource(R.string.hotel_checkout_room_empty)
        is HotelCheckoutValidationError.InvalidPaxType -> "Invalid guest type"
        is HotelCheckoutValidationError.MissingRateKey -> "Missing rate key"
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelCheckoutScreenPreview() {
    MaterialTheme {
        HotelCheckoutScreen(
            uiState = HotelCheckoutUiState(
                holderFirstName = "John",
                holderLastName = "Doe",
                hotelState = UiState.Success(
                    HotelDetails(
                        code = 1,
                        name = "Grand Palace Hotel",
                        description = "",
                        categoryName = "",
                        accommodationType = "",
                        address = "123 Main Street",
                        city = "London",
                        countryCode = "UK",
                        latitude = 0.0,
                        longitude = 0.0,
                        email = null,
                        web = null,
                        phones = emptyList(),
                        images = emptyList(),
                        facilities = emptyList(),
                        rooms = emptyList(),
                        minRate = 150.0,
                        maxRate = 200.0,
                        currency = "USD"
                    )
                ),
                rooms = listOf(
                    HotelBookingRoom(
                        rateKey = "rate_1",
                        paxes = listOf(
                            HotelBookingPax(name = "John", surname = "Doe", type = "AD", age = null, roomId = 1)
                        )
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelCheckoutScreenLoadingPreview() {
    MaterialTheme {
        HotelCheckoutScreen(
            uiState = HotelCheckoutUiState(
                hotelState = UiState.Loading
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelCheckoutScreenErrorPreview() {
    MaterialTheme {
        HotelCheckoutScreen(
            uiState = HotelCheckoutUiState(
                hotelState = UiState.Error(com.dev.utils.uitext.UiText.DynamicString("Failed to fetch hotel details"))
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HotelCheckoutScreenValidationErrorPreview() {
    MaterialTheme {
        HotelCheckoutScreen(
            uiState = HotelCheckoutUiState(
                holderFirstName = "",
                holderLastName = "",
                hotelState = UiState.Success(
                    HotelDetails(
                        code = 1,
                        name = "Grand Palace Hotel",
                        description = "",
                        categoryName = "",
                        accommodationType = "",
                        address = "123 Main Street",
                        city = "London",
                        countryCode = "UK",
                        latitude = 0.0,
                        longitude = 0.0,
                        email = null,
                        web = null,
                        phones = emptyList(),
                        images = emptyList(),
                        facilities = emptyList(),
                        rooms = emptyList(),
                        minRate = 150.0,
                        maxRate = 200.0,
                        currency = "USD"
                    )
                ),
                rooms = listOf(
                    HotelBookingRoom(
                        rateKey = "rate_1",
                        paxes = listOf(
                            HotelBookingPax(name = "", surname = "", type = "AD", age = null, roomId = 1)
                        )
                    )
                ),
                validationErrors = listOf(
                    HotelCheckoutValidationError.EmptyHolderFirstName,
                    HotelCheckoutValidationError.EmptyHolderLastName,
                    HotelCheckoutValidationError.EmptyPaxName(0, 0)
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}
