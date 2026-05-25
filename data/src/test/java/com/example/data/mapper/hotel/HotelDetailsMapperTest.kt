package com.example.data.mapper.hotel

import com.example.network.dto.hotel.HotelDetailsDataDto
import com.example.network.dto.hotel.HotelImageDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HotelDetailsMapperTest {

    @Test
    fun `map HotelDetailsDataDto to domain model successfully`() {
        val dto = HotelDetailsDataDto(
            code = 1,
            name = "Test Hotel",
            description = "Description",
            categoryName = "5 Star",
            accommodationType = "Resort",
            address = "123 Test St",
            city = "Test City",
            countryCode = "US",
            latitude = 10.0,
            longitude = 20.0,
            email = "test@test.com",
            web = "www.test.com",
            phones = null,
            images = listOf(HotelImageDto(url = "/image1.jpg", type = "GEN", order = 1)),
            facilities = null,
            rooms = null,
            minRate = 100.0,
            maxRate = 200.0,
            currency = "USD"
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.code)
        assertEquals("Test Hotel", domain.name)
        assertEquals("Description", domain.description)
        assertEquals("5 Star", domain.categoryName)
        assertEquals("Resort", domain.accommodationType)
        assertEquals("123 Test St", domain.address)
        assertEquals("Test City", domain.city)
        assertEquals("US", domain.countryCode)
        assertEquals(10.0, domain.latitude ?: 0.0, 0.0)
        assertEquals(20.0, domain.longitude ?: 0.0, 0.0)
        assertEquals("test@test.com", domain.email)
        assertEquals("www.test.com", domain.web)
        assertEquals(100.0, domain.minRate ?: 0.0, 0.0)
        assertEquals(200.0, domain.maxRate ?: 0.0, 0.0)
        assertEquals("USD", domain.currency)
        
        // Null lists should map to empty lists
        assertEquals(emptyList<Any>(), domain.phones)
        assertEquals(emptyList<Any>(), domain.facilities)
        assertEquals(emptyList<Any>(), domain.rooms)

        // Images should be mapped
        assertEquals(1, domain.images.size)
        assertNotNull(domain.images[0].url)
    }
}
