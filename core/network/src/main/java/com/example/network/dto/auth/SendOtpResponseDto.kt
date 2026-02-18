package com.example.network.dto.auth

data class SendOtpResponseDto(val status: Boolean, val message: String, val expiresOn: String)