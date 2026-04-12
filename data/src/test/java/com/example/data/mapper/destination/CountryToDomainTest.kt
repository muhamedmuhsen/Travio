package com.example.data.mapper.destination

import com.example.data.BuildConfig
import com.example.network.dto.destinations.Country
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryToDomainTest {

    @Test
    fun should_resolveRelativeImageUrlToAbsoluteFlagUrl_whenImageUrlIsPresent() {
        val dto = Country(
            countryID = 1,
            imageURL = "/Landmarks_Images/Egypt.jpg",
            name = "Egypt"
        )

        val result = dto.toDomain()

        assertEquals(1, result.countryID)
        assertEquals("${BuildConfig.IMAGE_BASE_URL.trimEnd('/')}/Landmarks_Images/Egypt.jpg", result.flagURL)
        assertEquals("Egypt", result.name)
    }

    @Test
    fun should_keepAbsoluteImageUrlAsIs_whenImageUrlIsAlreadyAbsolute() {
        val dto = Country(
            countryID = 3,
            imageURL = "https://cdn.example.com/countries/uae.jpg",
            name = "UAE"
        )

        val result = dto.toDomain()

        assertEquals("https://cdn.example.com/countries/uae.jpg", result.flagURL)
    }

    @Test
    fun should_useEmptyFlagUrl_whenImageUrlIsNull() {
        val dto = Country(
            countryID = 2,
            imageURL = null,
            name = "Morocco"
        )

        val result = dto.toDomain()

        assertEquals("", result.flagURL)
    }
}

