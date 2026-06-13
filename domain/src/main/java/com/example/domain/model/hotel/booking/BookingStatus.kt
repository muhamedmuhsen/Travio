package com.example.domain.model.hotel.booking

enum class BookingStatus(val value: String) {
    PENDING_PAYMENT("PendingPayment"),
    PROCESSING_WEBHOOK("ProcessingWebhook"),
    CONFIRMED("Confirmed"),
    PAYMENT_FAILED("PaymentFailed"),
    SUPPLIER_FAILED("SupplierFailed"),
    REFUND_ISSUED("RefundIssued"),
    REFUNDED("Refunded"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(status: String?): BookingStatus {
            return entries.find { it.value.equals(status, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
