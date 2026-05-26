package com.dev.hotel.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.utils.uistate.UiState
import com.example.designsystem.components.AppTextField
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
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.hotel_checkout_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(HotelCheckoutAction.BackClicked) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val hotelData = (uiState.hotelState as? UiState.Success)?.data
            if (hotelData != null && hotelData.minRate != null) {
                CheckoutBottomBar(
                    price = hotelData.minRate!!,
                    currency = hotelData.currency ?: "USD",
                    isSubmitting = uiState.isSubmitting || uiState.isPaymentProcessing,
                    onSubmitClick = { onAction(HotelCheckoutAction.SubmitCheckout) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
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
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.hotel_checkout_error_loading_details),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message.asString(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(24.dp))
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

            AnimatedVisibility(
                visible = uiState.isSubmitting || uiState.isPaymentProcessing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
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

@Composable
private fun CheckoutContent(
    hotelDetails: HotelDetails,
    uiState: HotelCheckoutUiState,
    onAction: (HotelCheckoutAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

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
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun HotelCheckoutSummaryCard(
    hotelDetails: HotelDetails,
    uiState: HotelCheckoutUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val thumbnail = hotelDetails.images.firstOrNull()?.url
            AsyncImage(
                model = thumbnail,
                contentDescription = "Hotel thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotelDetails.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = hotelDetails.address + ", " + hotelDetails.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CardMembership,
                    contentDescription = "Room details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.hotel_search_guests_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Holder details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.hotel_checkout_holder_section),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.hotel_checkout_room_header, roomIndex + 1),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

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
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            room.paxes.forEachIndexed { pIdx, pax ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val paxTypeLabel = if (pax.type == "AD") "Adult" else "Child"
                    Text(
                        text = stringResource(R.string.hotel_checkout_guest_header, pIdx + 1, paxTypeLabel),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

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
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    if (pax.type == "CH") {
                        Spacer(modifier = Modifier.height(8.dp))
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
                            placeholder = "Child Age",
                            isError = childAgeError != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (childAgeError != null) {
                            Text(
                                text = childAgeError.asString(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notes,
                    contentDescription = "Remarks Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.hotel_checkout_remarks_section),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val symbol = when (currency.uppercase()) {
                        "USD" -> "$"
                        "EUR" -> "€"
                        "GBP" -> "£"
                        else -> "$currency "
                    }
                    Text(
                        text = String.format("%s%.2f", symbol, price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = onSubmitClick,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .height(48.dp)
                        .width(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.hotel_checkout_button),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.hotel_checkout_taxes_fees),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
