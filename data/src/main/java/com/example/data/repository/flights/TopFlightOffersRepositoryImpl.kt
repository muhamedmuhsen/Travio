package com.example.data.repository.flights

import com.example.data.mapper.flights.toDomain
import com.example.data.utils.safeApiCall
import com.example.domain.model.flights.TopFlightOffer
import com.example.domain.repository.flights.TopFlightOffersRepository
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.network.api.FlightBookingApi
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopFlightOffersRepositoryImpl @Inject constructor(
    private val api: FlightBookingApi,
    private val cache: TopFlightOffersCache
) : TopFlightOffersRepository {

    override suspend fun getTopFlightOffers(
        forceRefresh: Boolean,
        limit: Int?
    ): Result<List<TopFlightOffer>, DataError> {
        try {
            if (!forceRefresh) {
                val cached = cache.get()
                if (!cached.isNullOrEmpty()) {
                    Timber.d("TopFlightOffersRepositoryImpl: returning cached offers count=${cached.size}")
                    return Result.Success(cached.takeIf { it.isNotEmpty() } ?: emptyList())
                }
            }

            val response = safeApiCall { api.getTopOffers() }
            return when (response) {
                is Result.Error -> {
                    Timber.w("TopFlightOffersRepositoryImpl: api call error=$response")
                    Result.Error(response.error)
                }
                is Result.Success -> {
                    val body = response.data
                    if (!body.success) {
                        Timber.w("TopFlightOffersRepositoryImpl: api returned success=false")
                        return Result.Error(DataError.Network.UnexpectedResponse)
                    }
                    val mapped = (body.data ?: emptyList())
                        .mapNotNull { dto -> dto.toDomain() }
                        .distinctBy { it.offerId }

                    cache.set(mapped)
                    val limited = limit?.let { mapped.take(it) } ?: mapped
                    Result.Success(limited)
                }
            }
        } catch (t: Throwable) {
            Timber.e(t, "TopFlightOffersRepositoryImpl: unexpected throwable")
            return Result.Error(DataError.UnknownError)
        }
    }
}
