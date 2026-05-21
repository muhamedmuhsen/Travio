package com.example.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Balcony
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.HeatPump
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WheelchairPickup
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.domain.model.hotel.Amenity

fun Amenity.getIcon(): ImageVector {
    return when (this) {
        Amenity.WIFI -> Icons.Default.Wifi
        Amenity.RECEPTION_24H -> Icons.Default.SupportAgent
        Amenity.RESTAURANT -> Icons.Default.Restaurant
        Amenity.BAR -> Icons.Default.LocalBar
        Amenity.BREAKFAST -> Icons.Default.FreeBreakfast
        Amenity.AIR_CONDITIONING -> Icons.Default.AcUnit
        Amenity.CENTRAL_HEATING -> Icons.Default.HeatPump
        Amenity.CAR_PARK -> Icons.Default.LocalParking
        Amenity.LIFT_ACCESS -> Icons.Default.Elevator
        Amenity.WHEELCHAIR_ACCESSIBLE -> Icons.Default.WheelchairPickup
        Amenity.OUTDOOR_POOL -> Icons.Default.Pool
        Amenity.INDOOR_POOL -> Icons.Default.Pool
        Amenity.GYM -> Icons.Default.FitnessCenter
        Amenity.SPA -> Icons.Default.Spa
        Amenity.ROOM_SERVICE -> Icons.Default.RoomService
        Amenity.LAUNDRY_SERVICE -> Icons.Default.LocalLaundryService
        Amenity.AIRPORT_SHUTTLE -> Icons.Default.AirportShuttle
        Amenity.NON_SMOKING -> Icons.Default.SmokeFree
        Amenity.SAFE -> Icons.Default.Lock
        Amenity.LUGGAGE_ROOM -> Icons.Default.Luggage
        Amenity.LATE_CHECKOUT -> Icons.Default.Schedule
        Amenity.SMALL_PETS -> Icons.Default.Pets
        Amenity.TV -> Icons.Default.Tv
        Amenity.BALCONY -> Icons.Default.Balcony
        Amenity.KITCHENETTE -> Icons.Default.Kitchen
        Amenity.CURRENCY_EXCHANGE -> Icons.Default.CurrencyExchange
        Amenity.MEDICAL_SERVICE -> Icons.Default.MedicalServices
        Amenity.BABYSITTING -> Icons.Default.ChildCare
        Amenity.BICYCLE_HIRE -> Icons.Default.PedalBike
        Amenity.CAR_HIRE -> Icons.Default.DirectionsBus
    }
}
