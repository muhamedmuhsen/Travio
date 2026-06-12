package com.example.common.extensions

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatCurrency(
    currencyCode: String,
    amount: Double
): String {
    if (currencyCode.isBlank()) return amount.formatTwoDecimals()

    return try {
        val currency = Currency.getInstance(currencyCode.uppercase())
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            this.currency = currency
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        formatter.format(amount)
    } catch (_: IllegalArgumentException) {
        "$currencyCode ${amount.formatTwoDecimals()}"
    }
}

private fun Double.formatTwoDecimals(): String = String.format(Locale.US, "%.2f", this)
