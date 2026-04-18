package com.dev.home.presentation

import com.dev.utils.uitext.UiText

data class HomePaginationState(
    val currentPageIndex: Int = 0,
    val pageSize: Int = 10,
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreError: UiText? = null
)
