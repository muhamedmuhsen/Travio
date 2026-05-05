package com.example.feature.booking.presentation

import com.example.domain.model.booking.BookingResult
import com.example.domain.model.booking.Passenger
import javax.annotation.concurrent.Immutable

@Immutable
data class BookingUiState(
    val passengers: List<Passenger> = listOf(Passenger("", "", "", "", "", "", "")),
    // Default as seen in image, should be set from offer data
    val totalPrice: String = "$1,248.50",
    val isProcessing: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle,
    val bookingResult: BookingResult? = null,
    val error: String? = null,
    val validationErrors: Map<Int, List<com.example.domain.utils.booking.PassengerValidationError>> = emptyMap()
)

enum class PaymentStatus {
    Idle,
    CreatingIntent,
    AwaitingConfirmation,
    Success,
    Failed,
    Canceled
}

sealed interface BookingEffect {
    data class NavigateToConfirmation(val pnr: String) : BookingEffect
    data class ShowError(val message: String) : BookingEffect
    data class ConfirmPayment(val clientSecret: String) : BookingEffect
}
