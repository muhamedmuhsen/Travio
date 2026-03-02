package com.example.domain.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SessionEvent {
    /** Fired when the refresh token is invalid / expired and the user must re-authenticate. */
    data object SessionExpired : SessionEvent
}

@Singleton
class SessionEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    fun emitSessionExpired() {
        _events.tryEmit(SessionEvent.SessionExpired)
    }
}
