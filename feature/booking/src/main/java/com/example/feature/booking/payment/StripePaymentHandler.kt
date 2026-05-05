package com.example.feature.booking.payment

import androidx.activity.ComponentActivity
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class StripePaymentHandler(
    private val activity: ComponentActivity,
    private val onResult: (PaymentSheetResult) -> Unit
) {
    private val paymentSheet = PaymentSheet(activity, ::onPaymentSheetResult)

    fun presentPaymentSheet(clientSecret: String) {
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "Travio"
        )
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration)
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        onResult(paymentSheetResult)
    }
}
