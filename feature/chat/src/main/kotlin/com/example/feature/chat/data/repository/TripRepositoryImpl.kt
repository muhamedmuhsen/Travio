package com.example.feature.chat.data.repository

import com.example.database.trips.TripActivityEntity
import com.example.database.trips.TripDayEntity
import com.example.database.trips.TripHotelEntity
import com.example.database.trips.TripPlanDao
import com.example.database.trips.TripPlanEntity
import com.example.feature.chat.domain.model.Hotel
import com.example.feature.chat.domain.model.TripActivity
import com.example.feature.chat.domain.model.TripDay
import com.example.feature.chat.domain.model.TripPlan
import com.example.feature.chat.domain.model.TripPlanStatus
import com.example.feature.chat.domain.repository.TripRepository
import com.example.network.api.AiApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripPlanDao: TripPlanDao,
    private val aiApi: AiApi
) : TripRepository {

    override suspend fun saveTripPlan(tripPlan: TripPlan) {
        val entity = TripPlanEntity(
            id = tripPlan.id,
            threadId = tripPlan.threadId,
            title = tripPlan.title,
            createdAt = tripPlan.createdAt,
            coverImage = tripPlan.coverImage,
            status = tripPlan.status.name,
            recommendedHotels = tripPlan.recommendedHotels.map {
                TripHotelEntity(
                    name = it.name,
                    description = it.description,
                    rating = it.rating,
                    address = it.address,
                    link = it.link,
                    imageUrl = it.imageUrl
                )
            },
            dailyPlans = tripPlan.dailyPlans.map {
                TripDayEntity(
                    day = it.day,
                    theme = it.theme,
                    activities = it.activities.map { act ->
                        TripActivityEntity(
                            type = act.type,
                            placeName = act.placeName,
                            suggestedTime = act.suggestedTime,
                            description = act.description,
                            address = act.address,
                            imageUrl = act.imageUrl
                        )
                    }
                )
            }
        )
        tripPlanDao.insertTripPlan(entity)
    }

    override fun observeTrips(): Flow<List<TripPlan>> {
        return tripPlanDao.getAllTripPlans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTripById(tripId: String): TripPlan? {
        val cached = tripPlanDao.getTripPlanById(tripId)?.toDomain()
        if (cached != null) {
            val isIncomplete = cached.dailyPlans.any { day ->
                day.activities.any { act -> act.type.isBlank() || act.imageUrl == null }
            }
            if (isIncomplete) {
                Timber.d(
                    "Trip %s in local DB has empty/null activities. " +
                        "Refreshing from network thread %s...",
                    tripId,
                    cached.threadId
                )
                try {
                    val apiResponse = aiApi.getAiStatus(cached.threadId)
                    val apiData = apiResponse.data
                    if (apiData != null) {
                        val itinerary = apiData.itinerary
                        if (!itinerary.isNullOrEmpty()) {
                            val firstHotelImage = apiData.recommendedHotels
                                ?.firstOrNull()?.imageUrl
                            val firstActivityImage = itinerary.firstOrNull()
                                ?.activities?.firstOrNull()?.imageUrl
                            val coverImage = firstActivityImage ?: firstHotelImage

                            val updatedTrip = TripPlan(
                                id = cached.id,
                                threadId = cached.threadId,
                                title = itinerary.firstOrNull()?.theme ?: cached.title,
                                createdAt = cached.createdAt,
                                coverImage = coverImage,
                                status = TripPlanStatus.COMPLETED,
                                recommendedHotels = apiData.recommendedHotels?.map { h ->
                                    Hotel(
                                        name = h.name ?: "",
                                        description = h.description,
                                        rating = h.rating,
                                        address = h.address,
                                        link = h.link,
                                        imageUrl = h.imageUrl
                                    )
                                } ?: emptyList(),
                                dailyPlans = itinerary.map { d ->
                                    TripDay(
                                        day = d.day ?: 1,
                                        theme = d.theme ?: "",
                                        activities = d.activities?.map { a ->
                                            TripActivity(
                                                type = a.type ?: "",
                                                placeName = a.placeName ?: "",
                                                suggestedTime = a.suggestedTime,
                                                description = a.description,
                                                address = a.address,
                                                imageUrl = a.imageUrl
                                            )
                                        } ?: emptyList()
                                    )
                                }
                            )
                            saveTripPlan(updatedTrip)
                            return updatedTrip
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh incomplete trip from REST API fallback")
                }
            }
        }
        return cached
    }

    override suspend fun getTripsForThread(threadId: String): List<TripPlan> {
        return tripPlanDao.getTripsForThread(threadId).map { it.toDomain() }
    }

    override suspend fun deleteTripPlan(tripId: String) {
        tripPlanDao.deleteTripPlanById(tripId)
    }

    private fun TripPlanEntity.toDomain(): TripPlan {
        return TripPlan(
            id = id,
            threadId = threadId,
            title = title,
            createdAt = createdAt,
            coverImage = coverImage,
            status = TripPlanStatus.valueOf(status),
            recommendedHotels = recommendedHotels.map {
                Hotel(
                    name = it.name,
                    description = it.description,
                    rating = it.rating,
                    address = it.address,
                    link = it.link,
                    imageUrl = it.imageUrl
                )
            },
            dailyPlans = dailyPlans.map {
                TripDay(
                    day = it.day,
                    theme = it.theme,
                    activities = it.activities.map { act ->
                        TripActivity(
                            type = act.type,
                            placeName = act.placeName,
                            suggestedTime = act.suggestedTime,
                            description = act.description,
                            address = act.address,
                            imageUrl = act.imageUrl
                        )
                    }
                )
            }
        )
    }
}
