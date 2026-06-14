package com.example.domain.model.booking

data class Passenger(
    val id: String,
    val title: String,
    val givenName: String,
    val familyName: String,
    val bornOn: String,
    val email: String,
    val phoneNumber: String,
    val gender: String
)
