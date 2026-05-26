package com.dev.hotel.checkout

sealed interface HotelCheckoutAction {
    data class UpdateHolderFirstName(val firstName: String) : HotelCheckoutAction
    data class UpdateHolderLastName(val lastName: String) : HotelCheckoutAction
    data class UpdateRemark(val remark: String) : HotelCheckoutAction
    data class UpdatePaxName(val roomIndex: Int, val paxIndex: Int, val name: String) : HotelCheckoutAction
    data class UpdatePaxSurname(val roomIndex: Int, val paxIndex: Int, val surname: String) : HotelCheckoutAction
    data class UpdatePaxAge(val roomIndex: Int, val paxIndex: Int, val age: Int?) : HotelCheckoutAction
    data object SubmitCheckout : HotelCheckoutAction
    data class PaymentCompleted(val bookingId: String) : HotelCheckoutAction
    data class PaymentFailed(val error: String) : HotelCheckoutAction
    data object PaymentCanceled : HotelCheckoutAction
    data object BackClicked : HotelCheckoutAction
}
