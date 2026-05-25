package com.example.domain.model.hotel

data class RoomRate(
    val rateKey: String,
    val rateClass: String?,
    val price: Double?,
    val boardCode: String?,
    val boardName: String?,
    val allotment: Int?,
    val cancellationPolicies: List<CancellationPolicy>
)
