package com.example.domain.model.hotel

enum class Amenity(val code: Int, val displayName: String) {
    WIFI(261, "Wi-fi"),
    RECEPTION_24H(30, "24-hour reception"),
    RESTAURANT(200, "Restaurant"),
    BAR(130, "Bar"),
    BREAKFAST(40, "Breakfast"),
    AIR_CONDITIONING(10, "Air conditioning in public areas"),
    CENTRAL_HEATING(190, "Central heating"),
    CAR_PARK(320, "Car park"),
    LIFT_ACCESS(70, "Lift access"),
    WHEELCHAIR_ACCESSIBLE(250, "Wheelchair-accessible"),
    OUTDOOR_POOL(306, "Outdoor swimming pool"),
    INDOOR_POOL(313, "Indoor swimming pool"),
    GYM(470, "Gym"),
    SPA(620, "Spa centre"),
    ROOM_SERVICE(270, "Room service"),
    LAUNDRY_SERVICE(280, "Laundry service"),
    AIRPORT_SHUTTLE(562, "Airport Shuttle"),
    NON_SMOKING(562, "Non-smoking establishment"), // Note: Code 562 is duplicated in user input
    SAFE(200, "Safe"), // Note: Code 200 is duplicated (Restaurant)
    LUGGAGE_ROOM(559, "Luggage room"),
    LATE_CHECKOUT(564, "Late Check-out"),
    SMALL_PETS(535, "Small pets allowed (under 5 kg)"),
    TV(55, "TV"),
    BALCONY(230, "Balcony"),
    KITCHENETTE(110, "Kitchenette"),
    CURRENCY_EXCHANGE(50, "Currency exchange facilities"),
    MEDICAL_SERVICE(290, "Medical service"),
    BABYSITTING(485, "Babysitting service"),
    BICYCLE_HIRE(310, "Bicycle hire service"),
    CAR_HIRE(490, "Car hire");

    companion object {
        fun fromCode(code: Int): Amenity? = entries.find { it.code == code }
    }
}
