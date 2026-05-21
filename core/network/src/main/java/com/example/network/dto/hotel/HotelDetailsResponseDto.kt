package com.example.network.dto.hotel

import com.google.gson.annotations.SerializedName

data class HotelDetailsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<String>?,
    @SerializedName("data") val data: HotelDetailsDataDto?
)

data class HotelDetailsDataDto(
    @SerializedName("code") val code: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("accommodationType") val accommodationType: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("countryCode") val countryCode: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("email") val email: String?,
    @SerializedName("web") val web: String?,
    @SerializedName("phones") val phones: List<HotelPhoneDto>?,
    @SerializedName("images") val images: List<HotelImageDto>?,
    @SerializedName("facilities") val facilities: List<HotelFacilityDto>?,
    @SerializedName("rooms") val rooms: List<HotelRoomDto>?,
    @SerializedName("minRate") val minRate: Double?,
    @SerializedName("maxRate") val maxRate: Double?,
    @SerializedName("currency") val currency: String?
)

data class HotelPhoneDto(
    @SerializedName("type") val type: String?,
    @SerializedName("number") val number: String?
)

data class HotelFacilityDto(
    @SerializedName("code") val code: Int?,
    @SerializedName("groupCode") val groupCode: Int?,
    @SerializedName("description") val description: String?
)

data class HotelRoomDto(
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("images") val images: List<HotelImageDto>?,
    @SerializedName("roomFacilities") val roomFacilities: List<String>?,
    @SerializedName("rates") val rates: List<RoomRateDto>?
)

data class RoomRateDto(
    @SerializedName("rateKey") val rateKey: String?,
    @SerializedName("rateClass") val rateClass: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("boardCode") val boardCode: String?,
    @SerializedName("boardName") val boardName: String?,
    @SerializedName("allotment") val allotment: Int?,
    @SerializedName("cancellationPolicies") val cancellationPolicies: List<CancellationPolicyDto>?
)

data class CancellationPolicyDto(
    @SerializedName("amount") val amount: Double?,
    @SerializedName("from") val from: String?
)
