package com.dev.hotel.checkout

sealed interface BookingSuccessAction {
    data object DoneClicked : BookingSuccessAction
}
