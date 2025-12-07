package com.example.feature.newpassword

data class NewPasswordState(
    val newPassword: String = "",
    val isPasswordsDoesnotMatch: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val confirmNewPassword: String = ""
)
