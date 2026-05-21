package com.example.data.mapper.hotel

import com.example.database.hotel.CachedCancellationPolicy
import com.example.database.hotel.CachedHotelDetails
import com.example.database.hotel.CachedHotelFacility
import com.example.database.hotel.CachedHotelPhone
import com.example.database.hotel.CachedHotelRoom
import com.example.database.hotel.CachedRoomRate
import com.example.domain.model.hotel.CancellationPolicy
import com.example.domain.model.hotel.HotelDetails
import com.example.domain.model.hotel.HotelFacility
import com.example.domain.model.hotel.HotelPhone
import com.example.domain.model.hotel.HotelRoom
import com.example.domain.model.hotel.RoomRate

fun CachedHotelDetails.toDomain(): HotelDetails {
    return HotelDetails(
        code = code,
        name = name,
        description = description,
        categoryName = categoryName,
        accommodationType = accommodationType,
        address = address,
        city = city,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        email = email,
        web = web,
        phones = phones.map { it.toDomain() },
        images = images.map { it.toDomain() },
        facilities = facilities.map { it.toDomain() },
        rooms = rooms.map { it.toDomain() },
        minRate = minRate,
        maxRate = maxRate,
        currency = currency
    )
}

fun CachedHotelPhone.toDomain(): HotelPhone = HotelPhone(type = type, number = number)

fun CachedHotelFacility.toDomain(): HotelFacility = HotelFacility(code = code, groupCode = groupCode, description = description)

fun CachedHotelRoom.toDomain(): HotelRoom =
    HotelRoom(
        code = code,
        name = name,
        images = images.map { it.toDomain() },
        roomFacilities = roomFacilities,
        rates = rates.map { it.toDomain() }
    )

fun CachedRoomRate.toDomain(): RoomRate =
    RoomRate(
        rateKey = rateKey,
        rateClass = rateClass,
        price = price,
        boardCode = boardCode,
        boardName = boardName,
        allotment = allotment,
        cancellationPolicies = cancellationPolicies.map { it.toDomain() }
    )

fun CachedCancellationPolicy.toDomain(): CancellationPolicy = CancellationPolicy(amount = amount, from = from)

fun HotelDetails.toCached(
    checkIn: String,
    checkOut: String,
    adults: Int,
    children: Int,
    childrenAges: String,
    timestamp: Long
): CachedHotelDetails {
    return CachedHotelDetails(
        code = code,
        name = name,
        description = description,
        categoryName = categoryName,
        accommodationType = accommodationType,
        address = address,
        city = city,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        email = email,
        web = web,
        phones = phones.map { it.toCached() },
        images = images.map { it.toCached() },
        facilities = facilities.map { it.toCached() },
        rooms = rooms.map { it.toCached() },
        minRate = minRate,
        maxRate = maxRate,
        currency = currency,
        searchCheckIn = checkIn,
        searchCheckOut = checkOut,
        searchAdults = adults,
        searchChildren = children,
        searchChildrenAges = childrenAges,
        timestamp = timestamp
    )
}

fun HotelPhone.toCached(): CachedHotelPhone = CachedHotelPhone(type = type, number = number)

fun HotelFacility.toCached(): CachedHotelFacility = CachedHotelFacility(code = code, groupCode = groupCode, description = description)

fun HotelRoom.toCached(): CachedHotelRoom =
    CachedHotelRoom(
        code = code,
        name = name,
        images = images.map { it.toCached() },
        roomFacilities = roomFacilities,
        rates = rates.map { it.toCached() }
    )

fun RoomRate.toCached(): CachedRoomRate =
    CachedRoomRate(
        rateKey = rateKey,
        rateClass = rateClass,
        price = price,
        boardCode = boardCode,
        boardName = boardName,
        allotment = allotment,
        cancellationPolicies = cancellationPolicies.map { it.toCached() }
    )

fun CancellationPolicy.toCached(): CachedCancellationPolicy = CachedCancellationPolicy(amount = amount, from = from)
