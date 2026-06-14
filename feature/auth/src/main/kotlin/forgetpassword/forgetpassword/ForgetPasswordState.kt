package com.example.feature.forgetpassword.forgetpassword

data class ForgetPasswordState(
    val email: String = "",
    val isEmailError: Boolean = false,
    val isLoading: Boolean = false
)
