package com.example.data.mapper.hotel

import com.example.domain.model.hotel.CancellationPolicy
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.model.hotel.HotelFacility
import com.example.domain.model.hotel.HotelPhone
import com.example.domain.model.hotel.HotelRoom
import com.example.domain.model.hotel.RoomRate
import com.example.network.dto.hotel.CancellationPolicyDto
import com.example.network.dto.hotel.HotelDetailsDataDto
import com.example.network.dto.hotel.HotelFacilityDto
import com.example.network.dto.hotel.HotelPhoneDto
import com.example.network.dto.hotel.HotelRoomDto
import com.example.network.dto.hotel.RoomRateDto

fun HotelDetailsDataDto.toDomain(): HotelDetails {
    return HotelDetails(
        code = this.code ?: 0,
        name = this.name ?: "Unknown Hotel",
        description = this.description,
        categoryName = this.categoryName,
        accommodationType = this.accommodationType,
        address = this.address,
        city = this.city,
        countryCode = this.countryCode,
        latitude = this.latitude,
        longitude = this.longitude,
        email = this.email,
        web = this.web,
        phones = this.phones?.mapNotNull { it.toDomain() } ?: emptyList(),
        images = this.images?.mapNotNull { it.toDomain() } ?: emptyList(),
        facilities = this.facilities?.mapNotNull { it.toDomain() } ?: emptyList(),
        rooms = this.rooms?.mapNotNull { it.toDomain() } ?: emptyList(),
        minRate = this.minRate,
        maxRate = this.maxRate,
        currency = this.currency
    )
}

fun HotelPhoneDto.toDomain(): HotelPhone? {
    if (this.number.isNullOrBlank()) return null
    return HotelPhone(
        type = this.type,
        number = this.number
    )
}

fun HotelFacilityDto.toDomain(): HotelFacility? {
    val code = this.code ?: return null
    val groupCode = this.groupCode ?: return null
    return HotelFacility(
        code = code,
        groupCode = groupCode,
        description = this.description
    )
}

fun HotelRoomDto.toDomain(): HotelRoom? {
    val code = this.code ?: return null
    val name = this.name ?: return null
    return HotelRoom(
        code = code,
        name = name,
        images = this.images?.mapNotNull { it.toDomain() } ?: emptyList(),
        roomFacilities = this.roomFacilities ?: emptyList(),
        rates = this.rates?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

fun RoomRateDto.toDomain(): RoomRate? {
    val rateKey = this.rateKey ?: return null
    return RoomRate(
        rateKey = rateKey,
        rateClass = this.rateClass,
        price = this.price,
        boardCode = this.boardCode,
        boardName = this.boardName,
        allotment = this.allotment,
        cancellationPolicies = this.cancellationPolicies?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

fun CancellationPolicyDto.toDomain(): CancellationPolicy {
    return CancellationPolicy(
        amount = this.amount,
        from = this.from
    )
}
