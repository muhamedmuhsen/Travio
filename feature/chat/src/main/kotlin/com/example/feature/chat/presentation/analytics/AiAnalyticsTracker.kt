package com.example.feature.chat.presentation.analytics

import timber.log.Timber
import javax.inject.Inject

interface AiAnalyticsTracker {
    fun trackAiGenerationFailed(
        threadId: String,
        errorType: String
    )
    fun trackAiRetryClicked(threadId: String)
    fun trackAiServiceUnavailable(threadId: String)
}

class DefaultAiAnalyticsTracker @Inject constructor() : AiAnalyticsTracker {

    override fun trackAiGenerationFailed(
        threadId: String,
        errorType: String
    ) {
        Timber.i("Analytics: ai_generation_failed [thread=%s, error=%s]", threadId, errorType)
    }

    override fun trackAiRetryClicked(threadId: String) {
        Timber.i("Analytics: ai_retry_clicked [thread=%s]", threadId)
    }

    override fun trackAiServiceUnavailable(threadId: String) {
        Timber.i("Analytics: ai_service_unavailable [thread=%s]", threadId)
    }
}
