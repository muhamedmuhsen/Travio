package com.example.domain.model.booking

data class PaymentIntentInfo(
    val clientSecret: String,
    val paymentIntentId: String
)
