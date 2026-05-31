package com.example.data.mapper.hotel.booking

import com.example.domain.model.hotel.booking.BookingStatus
import com.example.network.dto.hotel.booking.BookingItemDto
import org.junit.Assert.assertEquals
import org.junit.Test

class BookingMapperTest {

    @Test
    fun `toDomain should correctly map BookingItemDto to BookingItem`() {
        val dto = BookingItemDto(
            id = "019e761e-8ca6-711b-bf97-a72549b47f36",
            hotelbedsReference = "HB-12345",
            hotelName = "Amarante Pyramids",
            bookingStatus = "PendingPayment",
            checkIn = "2026-05-31",
            checkOut = "2026-06-01",
            totalPrice = 47.65,
            currency = "USD",
            createdAt = "2026-05-29T23:42:54.1261974"
        )

        val domain = dto.toDomain()

        assertEquals("019e761e-8ca6-711b-bf97-a72549b47f36", domain.reference)
        assertEquals("HB-12345", domain.hotelbedsReference)
        assertEquals("Amarante Pyramids", domain.hotelName)
        assertEquals(BookingStatus.PENDING_PAYMENT, domain.status)
        assertEquals("2026-05-31", domain.checkIn)
        assertEquals("2026-06-01", domain.checkOut)
        assertEquals(47.65, domain.totalPrice, 0.001)
        assertEquals("USD", domain.currency)
        assertEquals("2026-05-29T23:42:54.1261974", domain.bookingDate)
    }
}
