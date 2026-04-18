package com.example.data.mapper.destination

import com.example.data.BuildConfig
import com.example.network.dto.destinations.Destination
import com.example.network.dto.destinations.GetAllDestinationsResponse
import com.example.network.dto.destinations.Interest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationsPageMapperTest {

    @Test
    fun should_mapAllFieldsCorrectly_whenMappingToDestinationsPage() {
        val response = GetAllDestinationsResponse(
            count = 24,
            pageIndex = 2,
            pageSize = 10,
            data = listOf(destinationDto(id = 11, imageUrls = listOf("/images/1.jpg")))
        )

        val result = response.toDestinationsPage()

        assertEquals(24, result.count)
        assertEquals(2, result.pageIndex)
        assertEquals(10, result.pageSize)
        assertEquals(1, result.items.size)
        assertEquals(11, result.items.first().destinationID)
        assertEquals("Destination 11", result.items.first().name)
    }

    @Test
    fun should_mapEmptyItems_whenResponseDataIsEmpty() {
        val response = GetAllDestinationsResponse(
            count = 0,
            pageIndex = 1,
            pageSize = 10,
            data = emptyList()
        )

        val result = response.toDestinationsPage()

        assertTrue(result.items.isEmpty())
        assertEquals(0, result.count)
    }

    @Test
    fun should_resolveImageUrlsInItems_whenMappingToDestinationsPage() {
        val base = BuildConfig.IMAGE_BASE_URL.trimEnd('/')
        val response = GetAllDestinationsResponse(
            count = 2,
            pageIndex = 1,
            pageSize = 10,
            data = listOf(
                destinationDto(
                    id = 1,
                    imageUrls = listOf("/Images/first.jpg", "Images/second.jpg", "https://cdn.example.com/third.jpg")
                )
            )
        )

        val result = response.toDestinationsPage()

        assertEquals(
            listOf("$base/Images/first.jpg", "$base/Images/second.jpg", "https://cdn.example.com/third.jpg"),
            result.items.first().imageUrls
        )
    }

    private fun destinationDto(id: Int, imageUrls: List<String>): Destination {
        return Destination(
            cityName = "Cairo",
            description = "Description",
            destinationID = id,
            imageUrls = imageUrls,
            interests = listOf(Interest(interestID = 1, interestName = "History")),
            latitude = 30.0444,
            longitude = 31.2357,
            name = "Destination $id",
            rating = 4.8,
            totalReviews = 1200
        )
    }
}

