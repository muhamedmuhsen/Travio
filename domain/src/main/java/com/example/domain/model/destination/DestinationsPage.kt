package com.example.domain.model.destination

data class DestinationsPage(
    val pageIndex: Int,
    val pageSize: Int,
    val count: Int,
    val items: List<Destination>
)
