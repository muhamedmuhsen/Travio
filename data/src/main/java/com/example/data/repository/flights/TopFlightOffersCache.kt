package com.example.data.repository.flights

import com.example.domain.model.flights.TopFlightOffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopFlightOffersCache @Inject constructor() {

    private data class CacheEntry(val timestamp: Long, val offers: List<TopFlightOffer>)

    // volatile is fine for simple single-writer/single-reader scenarios
    @Volatile
    private var entry: CacheEntry? = null

    private val ttlMs: Long = 10 * 60 * 1000 // 10 minutes

    fun get(): List<TopFlightOffer>? {
        val current = entry ?: return null
        if (System.currentTimeMillis() - current.timestamp > ttlMs) {
            entry = null
            return null
        }
        return current.offers
    }

    fun set(offers: List<TopFlightOffer>) {
        entry = CacheEntry(System.currentTimeMillis(), offers)
    }

    fun invalidate() {
        entry = null
    }
}
