package com.example.common.baseresponse

data class BaseResponse<T>(
    val data: T,
    val success: Boolean,
    val message: String,
    val errors: List<String>
)
