package com.example.data.repository.favorite

import com.example.data.favorite.FavoriteTestFixtures
import com.example.domain.model.favorite.FavoriteMutationResult
import com.example.domain.model.favorite.FavoritesPage
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FavoritesApi
import com.example.network.dto.favorite.FavoritesPageDto
import com.example.network.dto.favorite.FavoritesResponseDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteDestinationRepositoryImplTest {

    @Test
    fun givenSuccessfulEnvelope_whenGetPage_thenMapsFavoritesPage() = runTest {
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                pageResponse = FavoriteTestFixtures.pageEnvelope(
                    success = true,
                    data = FavoriteTestFixtures.pageDto(
                        pageIndex = 1,
                        pageSize = 2,
                        count = 4,
                        items = listOf(
                            FavoriteTestFixtures.destinationDto(destinationId = 10),
                            FavoriteTestFixtures.destinationDto(destinationId = 11)
                        )
                    )
                )
            ),
            syncStore = FavoriteSyncStore()
        )

        val result = repository.getFavoriteDestinationsPage(pageIndex = 1, pageSize = 2)

        assertTrue(result is Result.Success)
        val page = (result as Result.Success<FavoritesPage, DataError>).data
        assertEquals(1, page.pageIndex)
        assertEquals(2, page.pageSize)
        assertEquals(4, page.count)
        assertEquals(listOf(10, 11), page.items.map { it.destinationId })
    }

    @Test
    fun givenEnvelopeFailure_whenGetPage_thenReturnsUnexpectedResponse() = runTest {
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                pageResponse = FavoriteTestFixtures.pageEnvelope(success = false)
            ),
            syncStore = FavoriteSyncStore()
        )

        val result = repository.getFavoriteDestinationsPage(pageIndex = 1, pageSize = 10)

        assertEquals(Result.Error<FavoritesPage, DataError>(DataError.Network.UnexpectedResponse), result)
    }

    @Test
    fun givenPartialImagePayload_whenGetPage_thenFiltersBlankImages() = runTest {
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                pageResponse = FavoriteTestFixtures.pageEnvelope(
                    data = FavoriteTestFixtures.pageDto(
                        items = listOf(
                            FavoriteTestFixtures.destinationDto(
                                destinationId = 5,
                                imageUrls = listOf("", " ", "https://example.com/valid.jpg")
                            )
                        )
                    )
                )
            ),
            syncStore = FavoriteSyncStore()
        )

        val result = repository.getFavoriteDestinationsPage(pageIndex = 1, pageSize = 10)

        assertTrue(result is Result.Success)
        val first = (result as Result.Success<FavoritesPage, DataError>).data.items.first()
        assertEquals(listOf("https://example.com/valid.jpg"), first.imageUrls)
    }

    @Test
    fun givenExistingStaleIds_whenFirstPageFetched_thenSyncStoreReplacedWithFetchedIds() = runTest {
        val syncStore = FavoriteSyncStore()
        syncStore.setFavoriteIds(setOf(99, 100))
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                pageResponse = FavoriteTestFixtures.pageEnvelope(
                    success = true,
                    data = FavoriteTestFixtures.pageDto(
                        pageIndex = 1,
                        pageSize = 2,
                        count = 2,
                        items = listOf(
                            FavoriteTestFixtures.destinationDto(destinationId = 10),
                            FavoriteTestFixtures.destinationDto(destinationId = 11)
                        )
                    )
                )
            ),
            syncStore = syncStore
        )

        repository.getFavoriteDestinationsPage(pageIndex = 1, pageSize = 2)

        assertEquals(setOf(10, 11), syncStore.observeFavoriteIds().value)
    }

    @Test
    fun givenFirstPageThenSecondPage_whenFetched_thenSecondPageIdsMergeIntoStore() = runTest {
        val syncStore = FavoriteSyncStore()
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                pageResponsesByIndex = mapOf(
                    1 to FavoriteTestFixtures.pageEnvelope(
                        success = true,
                        data = FavoriteTestFixtures.pageDto(
                            pageIndex = 1,
                            pageSize = 2,
                            count = 4,
                            items = listOf(
                                FavoriteTestFixtures.destinationDto(destinationId = 10),
                                FavoriteTestFixtures.destinationDto(destinationId = 11)
                            )
                        )
                    ),
                    2 to FavoriteTestFixtures.pageEnvelope(
                        success = true,
                        data = FavoriteTestFixtures.pageDto(
                            pageIndex = 2,
                            pageSize = 2,
                            count = 4,
                            items = listOf(
                                FavoriteTestFixtures.destinationDto(destinationId = 12),
                                FavoriteTestFixtures.destinationDto(destinationId = 13)
                            )
                        )
                    )
                )
            ),
            syncStore = syncStore
        )

        repository.getFavoriteDestinationsPage(pageIndex = 1, pageSize = 2)
        repository.getFavoriteDestinationsPage(pageIndex = 2, pageSize = 2)

        assertEquals(setOf(10, 11, 12, 13), syncStore.observeFavoriteIds().value)
    }

    @Test
    fun givenMutationEnvelopeSuccessWithFalseData_whenAddFavorite_thenReturnsSuccess() = runTest {
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                mutationResponse = FavoriteTestFixtures.mutationEnvelope(
                    success = true,
                    data = false,
                    message = "Favorite added successfully."
                )
            ),
            syncStore = FavoriteSyncStore()
        )

        val result = repository.addDestinationToFavorites(destinationId = 2)

        assertTrue(result is Result.Success)
        val mutation = (result as Result.Success<FavoriteMutationResult, DataError>).data
        assertTrue(mutation.isSuccess)
        assertEquals("Favorite added successfully.", mutation.message)
    }

    @Test
    fun givenMutationEnvelopeSuccessWithFalseData_whenRemoveFavorite_thenReturnsSuccess() = runTest {
        val repository = FavoriteDestinationRepositoryImpl(
            favoritesApi = FakeFavoritesApi(
                mutationResponse = FavoriteTestFixtures.mutationEnvelope(
                    success = true,
                    data = false,
                    message = "Favorite removed successfully."
                )
            ),
            syncStore = FavoriteSyncStore().apply { markFavorite(2) }
        )

        val result = repository.removeDestinationFromFavorites(destinationId = 2)

        assertTrue(result is Result.Success)
        val mutation = (result as Result.Success<FavoriteMutationResult, DataError>).data
        assertTrue(mutation.isSuccess)
        assertEquals("Favorite removed successfully.", mutation.message)
    }

    private class FakeFavoritesApi(
        private val pageResponse: FavoritesResponseDto<FavoritesPageDto> = FavoriteTestFixtures.pageEnvelope(),
        private val pageResponsesByIndex: Map<Int, FavoritesResponseDto<FavoritesPageDto>> = emptyMap(),
        private val mutationResponse: FavoritesResponseDto<Boolean> = FavoriteTestFixtures.mutationEnvelope()
    ) : FavoritesApi {

        override suspend fun getFavorites(pageIndex: Int, pageSize: Int): FavoritesResponseDto<FavoritesPageDto> {
            return pageResponsesByIndex[pageIndex] ?: pageResponse
        }

        override suspend fun addFavorite(destinationId: Int): FavoritesResponseDto<Boolean> {
            return mutationResponse
        }

        override suspend fun removeFavorite(destinationId: Int): FavoritesResponseDto<Boolean> {
            return mutationResponse
        }
    }
}


