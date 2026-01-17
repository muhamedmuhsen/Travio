package com.example.feature.code

data class CodeState(
    val code: List<String> = List(6) { "0" },
    val isCodeError: Boolean = false,
    val isCodeFilled: Boolean = false,
)
