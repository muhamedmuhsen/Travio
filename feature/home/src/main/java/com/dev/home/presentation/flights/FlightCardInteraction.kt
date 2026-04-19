package com.dev.home.presentation.flights

import androidx.compose.runtime.Immutable

@Immutable
data class FlightCardActionState(
    val label: String,
    val state: FlightCtaState,
    val enabled: Boolean,
    val loadingIndicatorVisible: Boolean
) {
    init {
        require(label.isNotBlank()) { "label cannot be blank" }
        if (state == FlightCtaState.LOADING) {
            require(!enabled) { "loading CTA must be disabled" }
            require(loadingIndicatorVisible) { "loading CTA must show indicator" }
        }
        if (state == FlightCtaState.DISABLED) {
            require(!enabled) { "disabled CTA must not be enabled" }
        }
    }

    companion object {
        fun default(label: String): FlightCardActionState {
            return FlightCardActionState(
                label = label,
                state = FlightCtaState.DEFAULT,
                enabled = true,
                loadingIndicatorVisible = false
            )
        }

        fun disabled(label: String): FlightCardActionState {
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
