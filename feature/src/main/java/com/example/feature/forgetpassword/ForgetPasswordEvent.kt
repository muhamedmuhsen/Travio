package com.example.feature.forgetpassword

sealed interface ForgetPasswordEvent {
    data object NavigateToCodeScreen : ForgetPasswordEvent
    data object ContactUs : ForgetPasswordEvent
}