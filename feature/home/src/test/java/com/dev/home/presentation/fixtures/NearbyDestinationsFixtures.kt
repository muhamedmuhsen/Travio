package com.dev.home.presentation.fixtures

import com.example.domain.model.destination.Destination
import com.example.domain.model.destination.Interest

object NearbyDestinationsFixtures {

    data class DistanceCase(
        val label: String,
        val destination: Destination,
        val distanceKm: Double,
        val expectedWithinRadius: Boolean
    )

    fun defaultNearbyList(): List<Destination> {
        return listOf(
            destination(id = 101, name = "Museum District", latitude = 30.051, longitude = 31.239),
            destination(id = 102, name = "River Walk", latitude = 30.062, longitude = 31.244),
            destination(id = 103, name = "Old Market", latitude = 30.074, longitude = 31.255)
        )
    }

    fun radiusBoundaryCases(radiusKm: Double = 50.0): List<DistanceCase> {
        return listOf(
            DistanceCase(
                label = "inside",
                destination = destination(id = 201, name = "Inside Radius", latitude = 30.0001, longitude = 31.0001),
                distanceKm = radiusKm - 0.01,
                expectedWithinRadius = true
            ),
            DistanceCase(
                label = "edge",
                destination = destination(id = 202, name = "Edge Radius", latitude = 30.0, longitude = 31.0),
                distanceKm = radiusKm,
                expectedWithinRadius = true
            ),
            DistanceCase(
                label = "outside",
                destination = destination(id = 203, name = "Outside Radius", latitude = 30.2, longitude = 31.2),
                distanceKm = radiusKm + 0.01,
                expectedWithinRadius = false
            )
        )
    }

    fun destination(
        id: Int,
        name: String = "Destination $id",
        latitude: Double = 30.0,
        longitude: Double = 31.0
    ): Destination {
        return Destination(
            cityName = "Cairo",
            description = "Fixture destination $id",
            destinationID = id,
            imageUrls = listOf("https://example.com/$id.jpg"),
            interests = listOf(Interest(interestID = 1, interestName = "Culture")),
            latitude = latitude,
            longitude = longitude,
            name = name,
            rating = 4.4,
            totalReviews = 55
        )
    }
}

