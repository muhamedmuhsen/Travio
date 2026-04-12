package com.example.data.mapper.destination

import com.example.data.BuildConfig
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.Interest
import org.junit.Assert.assertEquals
import org.junit.Test

class DestinationToDomainTest {

    @Test
    fun should_resolveRelativeImageUrlsToAbsolute_whenMappingDestination() {
        val dto = Destination(
            cityName = "Cairo",
            description = "Great place",
            destinationID = 101,
            imageUrls = listOf("/Images/pyramid.jpg", "Images/sphinx.jpg"),
            interests = listOf(Interest(1, "History")),
            latitude = 30.0444,
            longitude = 31.2357,
            name = "Pyramids",
            rating = 4.8,
            totalReviews = 1000
        )

        val result = dto.toDomain()
        val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')

        assertEquals(listOf("$base/Images/pyramid.jpg", "$base/Images/sphinx.jpg"), result.imageUrls)
    }

    @Test
    fun should_keepAbsoluteImageUrlsAsIs_whenMappingDestination() {
        val dto = Destination(
            cityName = "Paris",
            description = "City of lights",
            destinationID = 202,
            imageUrls = listOf("https://cdn.example.com/eiffel.jpg"),
            interests = listOf(Interest(2, "Culture")),
            latitude = 48.8566,
            longitude = 2.3522,
            name = "Eiffel Tower",
            rating = 4.7,
            totalReviews = 800
        )

        val result = dto.toDomain()

        assertEquals(listOf("https://cdn.example.com/eiffel.jpg"), result.imageUrls)
    }
}

