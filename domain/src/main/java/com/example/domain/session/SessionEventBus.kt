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
    // SharedFlow is used intentionally here instead of Channel/receiveAsFlow().
    // Reason: this bus is a @Singleton that lives outside any ViewModel lifecycle. Multiple
    // independent collectors (e.g. NavGraph observer, token interceptor) need to receive the
    // same event concurrently, which Channel cannot support — it delivers each item to only
    // one collector.
    //
    // Drop-risk trade-off: MutableSharedFlow with extraBufferCapacity = 1 means a single
    // un-collected event can be buffered. If no collector is active when tryEmit() is called
    // (e.g. app is backgrounded with the UI fully stopped), the event is dropped silently.
    // This is acceptable for SessionExpired because:
    //   1. The next authenticated API call will fail again and re-emit the event once the UI
    //      is back in the foreground and a collector is active.
    //   2. The alternative (replay = 1) would re-deliver a stale expiry event to every new
    //      collector (e.g. after re-login), which is a worse failure mode.
    private val _events = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    fun emitSessionExpired() {
        _events.tryEmit(SessionEvent.SessionExpired)
    }
}
