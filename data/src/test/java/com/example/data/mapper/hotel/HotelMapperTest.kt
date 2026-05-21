package com.example.data.mapper.hotel

import com.example.network.dto.hotel.HotelDto
import com.example.network.dto.hotel.HotelImageDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HotelMapperTest {

    @Test
    fun givenHotelImageDtoWithUrl_whenToDomain_thenMapsCorrectly() {
        val dto = HotelImageDto(
            url = "https://example.com/image.jpg",
            type = "primary",
            order = 1
        )

        val domain = dto.toDomain()

        assertNotNull(domain)
        assertEquals("https://example.com/image.jpg", domain!!.url)
        assertEquals("primary", domain.type)
        assertEquals(1, domain.order)
    }

    @Test
    fun givenHotelImageDtoWithNullUrl_whenToDomain_thenReturnsNull() {
        val dto = HotelImageDto(
            url = null,
            type = "primary",
            order = 1
        )

        val domain = dto.toDomain()

        assertNull(domain)
    }

    @Test
    fun givenHotelDto_whenToDomain_thenMapsCorrectly() {
        val dto = HotelDto(
            code = 12345,
            name = "Grand Palace Hotel",
            categoryName = "5 Star",
            destinationName = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357,
            minRate = 150.0,
            maxRate = 250.0,
            currency = "USD",
            thumbnailImage = "https://example.com/thumb.jpg",
            images = listOf(
                HotelImageDto(url = "https://example.com/image1.jpg", type = "gallery", order = 1),
                HotelImageDto(url = null, type = "gallery", order = 2) // should be filtered out
            )
        )

        val domain = dto.toDomain()

        assertEquals(12345, domain.code)
        assertEquals("Grand Palace Hotel", domain.name)
        assertEquals("5 Star", domain.categoryName)
        assertEquals("Cairo", domain.destinationName)
        assertEquals(30.0444, domain.latitude ?: 0.0, 0.0)
        assertEquals(31.2357, domain.longitude ?: 0.0, 0.0)
        assertEquals(150.0, domain.minRate ?: 0.0, 0.0)
        assertEquals(250.0, domain.maxRate ?: 0.0, 0.0)
        assertEquals("USD", domain.currency)
        assertEquals("https://example.com/thumb.jpg", domain.thumbnailImage)
        assertEquals(1, domain.images.size)
        assertEquals("https://example.com/image1.jpg", domain.images[0].url)
    }

    @Test
    fun givenHotelDtoWithNullName_whenToDomain_thenUsesFallbackName() {
        val dto = HotelDto(
            code = 12345,
            name = null,
            categoryName = null,
            destinationName = null,
            latitude = null,
            longitude = null,
            minRate = null,
            maxRate = null,
            currency = null,
            thumbnailImage = null,
            images = null
        )

        val domain = dto.toDomain()

        assertEquals(12345, domain.code)
        assertEquals("Unknown Hotel", domain.name)
        assertNull(domain.categoryName)
        assertNull(domain.destinationName)
        assertNull(domain.latitude)
        assertNull(domain.longitude)
        assertNull(domain.minRate)
        assertNull(domain.maxRate)
        assertNull(domain.currency)
        assertNull(domain.thumbnailImage)
        assertTrue(domain.images.isEmpty())
    }
}
