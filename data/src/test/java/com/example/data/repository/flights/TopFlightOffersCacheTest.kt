package com.example.data.repository.flights

import com.example.domain.model.flights.TopFlightOffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.lang.reflect.Field

class TopFlightOffersCacheTest {

    private fun sampleOffer(id: String) = TopFlightOffer(
        offerId = id,
        airlineName = "A",
        imageUrl = "",
        origin = "AAA",
        originCityName = "O",
        destination = "BBB",
        destinationCityName = "D",
        duration = "1h",
        flightNumber = "F1",
        airlineLogoUrl = "",
        stops = 0,
        cheapestPrice = 10.0,
        currency = "USD"
    )

    @Test
    fun given_freshEntry_when_get_then_returns() {
        val cache = TopFlightOffersCache()
        val offers = listOf(sampleOffer("1"))
        cache.set(offers)

        val got = cache.get()
        assertEquals(1, got?.size)
    }

    @Test
    fun given_invalidate_when_get_then_returnsNull() {
        val cache = TopFlightOffersCache()
        cache.set(listOf(sampleOffer("1")))
        cache.invalidate()
        val got = cache.get()
        assertNull(got)
    }

    @Test
    fun given_expiredEntry_when_get_then_returnsNull() {
        val cache = TopFlightOffersCache()
        cache.set(listOf(sampleOffer("1")))

        // Use reflection to set a stale entry timestamp
        val entryField: Field = cache.javaClass.getDeclaredField("entry")
        entryField.isAccessible = true
        val entryObj = entryField.get(cache)
        val entryClass = entryObj.javaClass
        val tsField = entryClass.getDeclaredField("timestamp")
        tsField.isAccessible = true
        // set timestamp far in the past
        tsField.setLong(entryObj, 0L)

        val got = cache.get()
        assertNull(got)
    }
}
