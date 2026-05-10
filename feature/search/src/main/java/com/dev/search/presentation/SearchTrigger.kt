package com.dev.search.presentation

internal data class SearchTrigger(
    val query: String,
    val isImmediate: Boolean,
    val selectedInterestIds: List<Int> = emptyList()
)
