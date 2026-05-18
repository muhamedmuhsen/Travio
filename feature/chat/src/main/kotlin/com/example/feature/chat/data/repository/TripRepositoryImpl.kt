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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripPlanDao: TripPlanDao
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
        return tripPlanDao.getTripPlanById(tripId)?.toDomain()
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
