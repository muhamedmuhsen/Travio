package com.example.feature.booking.presentation

import com.example.domain.model.booking.Passenger
import javax.annotation.concurrent.Immutable

@Immutable
data class BookingUiState(
    val passengers: List<Passenger> = emptyList(),
    val basePrice: Double = 0.0,
    val totalPrice: String = "",
    val isProcessing: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.Idle,
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
