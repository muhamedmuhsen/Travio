package com.example.feature.forgetpassword

data class ForgetPasswordState(
    val email: String = "",
    val isEmailError: Boolean = false,
)
