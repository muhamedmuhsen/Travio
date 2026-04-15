package com.example.domain.model.favorite

data class FavoriteMutationResult(
    val isSuccess: Boolean,
    val message: String?,
    val errors: List<String>
)
