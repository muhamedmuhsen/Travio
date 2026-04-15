package com.example.data.mapper.favorite

import com.example.data.favorite.FavoriteTestFixtures
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteDestinationMapperTest {

    @Test
    fun givenRelativeImagePath_whenMapped_thenResolvesWithBaseUrlOrRootPrefix() {
        val dto = FavoriteTestFixtures.destinationDto(
            imageUrls = listOf("images/favorites/paris.jpg", "")
        )

        val mapped = dto.toDomain()
        val firstUrl = mapped.imageUrls.firstOrNull().orEmpty()

        assertTrue(firstUrl.endsWith("/images/favorites/paris.jpg"))
    }
}

