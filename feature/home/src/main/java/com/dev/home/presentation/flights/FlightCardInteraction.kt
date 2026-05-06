package com.dev.home.presentation.flights

import androidx.compose.runtime.Immutable
import com.dev.utils.uitext.UiText

@Immutable
data class FlightCardActionState(
    val label: UiText,
    val state: FlightCtaState,
    val enabled: Boolean,
    val loadingIndicatorVisible: Boolean
) {
    init {
        if (state == FlightCtaState.LOADING) {
            require(!enabled) { "CTA must be disabled when in LOADING state" }
            require(loadingIndicatorVisible) { "Loading indicator must be visible when in LOADING state" }
        }
        require(label.isNotBlank()) { "label cannot be blank" }
    }

    companion object {
        fun default(label: UiText): FlightCardActionState {
            return FlightCardActionState(
                label = label,
                state = FlightCtaState.DEFAULT,
                enabled = true,
                loadingIndicatorVisible = false
            )
        }

        fun disabled(label: UiText): FlightCardActionState {
            return FlightCardActionState(
                label = label,
                state = FlightCtaState.DISABLED,
                enabled = false,
                loadingIndicatorVisible = false
            )
        }

        fun transition(
            current: FlightCardActionState,
            next: FlightCtaState
        ): FlightCardActionState {
            return when (next) {
                FlightCtaState.DEFAULT -> FlightCardActionState(
                    label = current.label,
                    state = FlightCtaState.DEFAULT,
                    enabled = true,
                    loadingIndicatorVisible = false
                )

                FlightCtaState.PRESSED -> FlightCardActionState(
                    label = current.label,
                    state = FlightCtaState.PRESSED,
                    enabled = true,
                    loadingIndicatorVisible = false
                )

                FlightCtaState.LOADING -> FlightCardActionState(
                    label = current.label,
                    state = FlightCtaState.LOADING,
                    enabled = false,
                    loadingIndicatorVisible = true
                )

                FlightCtaState.DISABLED -> disabled(label = current.label)
            }
        }
    }
}

@Immutable
data class FlightCardInteractionContract(
    val onCardTapAction: FlightCardTapAction = FlightCardTapAction.OPEN_DETAILS,
    val onCtaTapAction: FlightCardCtaTapAction = FlightCardCtaTapAction.START_BOOKING
)

enum class FlightCtaState {
    DEFAULT,
    PRESSED,
    LOADING,
    DISABLED
}

enum class FlightCardTapAction {
    OPEN_DETAILS
}

enum class FlightCardCtaTapAction {
    START_BOOKING
}
