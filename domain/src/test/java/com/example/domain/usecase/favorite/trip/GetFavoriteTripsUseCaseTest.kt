package com.example.domain.usecase.favorite.trip

import com.example.domain.model.favorite.Trip
import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoriteTripsUseCaseTest {

    @Test
    fun givenRepositoryTrips_whenInvoke_thenReturnsTripsInRepositoryOrder() = runTest {
        val expected = listOf(
            Trip(id = 2, title = "Newest", ownerName = "A", imageUrl = "", savedAt = "2026-04-11T10:00:00Z"),
            Trip(id = 1, title = "Older", ownerName = "B", imageUrl = "", savedAt = "2026-04-10T10:00:00Z")
        )
        val useCase = GetAllTripsUseCase(FakeFavoriteTripRepository(expected))

        val actual = useCase().first()

        assertEquals(expected, actual)
    }

    private class FakeFavoriteTripRepository(
        private val items: List<Trip>
    ) : FavoriteTripRepository {
        override fun getFavoriteTrips(): Flow<List<Trip>> = flowOf(items)

        override fun isTripFavorite(tripId: String): Flow<Boolean> = flowOf(true)

        override suspend fun addTripToFavorite(trip: Trip): Result<Unit, DataError.Local> = Result.Success(Unit)

        override suspend fun deleteTripFromFavorite(tripId: String): Result<Unit, DataError.Local> = Result.Success(Unit)
    }
}


