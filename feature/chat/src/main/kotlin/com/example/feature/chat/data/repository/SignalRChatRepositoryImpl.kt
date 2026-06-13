package com.example.feature.chat.data.repository

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import com.example.feature.chat.data.di.ChatRepositoryScope
import com.example.feature.chat.data.mapper.toChatMessage
import com.example.feature.chat.data.mapper.toFeatureDto
import com.example.feature.chat.data.mapper.toPlanGenerationState
import com.example.feature.chat.data.remote.SignalRService
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.ConnectionState
import com.example.feature.chat.domain.model.Hotel
import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.feature.chat.domain.model.TripActivity
import com.example.feature.chat.domain.model.TripDay
import com.example.feature.chat.domain.model.TripPlan
import com.example.feature.chat.domain.model.TripPlanStatus
import com.example.feature.chat.domain.repository.ChatRepository
import com.example.feature.chat.domain.repository.TripRepository
import com.example.network.api.AiApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SignalRChatRepositoryImpl @Inject constructor(
    private val signalRService: SignalRService,
    private val tripRepository: TripRepository,
    private val aiApi: AiApi,
    @ChatRepositoryScope private val repositoryScope: kotlinx.coroutines.CoroutineScope
) : ChatRepository {

    init {
        repositoryScope.launch {
            signalRService.observePlanStatus().collect { status ->
                Timber.d("SignalR plan status event collected in Repository: $status")
                if (status.isCompleted && status.data != null && status.tripId != null) {
                    saveTripIfNew(status.threadId, status.tripId, status.data)
                }
            }
        }
    }

    override fun observeMessages(threadId: String): Flow<ChatMessage> {
        return signalRService.observeMessages()
            .map { it.toChatMessage() }
    }

    override suspend fun sendMessage(
        threadId: String,
        content: String
    ): Result<Unit, DataError> {
        return try {
            signalRService.sendMessage(threadId, content)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.UnknownError)
        }
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return signalRService.observeConnectionState()
    }

    override fun observePlanStatus(threadId: String): Flow<PlanGenerationState> {
        val signalRFlow = signalRService.observePlanStatus()
            .map { it.toPlanGenerationState() }
            .filter { it.threadId == threadId }

        return kotlinx.coroutines.flow.flow {
            try {
                val apiResponse = aiApi.getAiStatus(threadId)
                Timber.d(
                    "REST API getAiStatus response: status=${apiResponse.status}, message=${apiResponse.message}, data=${apiResponse.data}"
                )
                val itinerary = apiResponse.data?.itinerary
                if (!itinerary.isNullOrEmpty()) {
                    Timber.d("Retrieved itinerary with ${itinerary.size} days: $itinerary")
                    val existingTrips = tripRepository.getTripsForThread(threadId)
                    val completedTrip = existingTrips.find { it.status == TripPlanStatus.COMPLETED }

                    // If we don't have a completed trip locally, we should prefer waiting for SignalR
                    // so we can get the real Integer ID, rather than instantly generating a UUID.
                    if (completedTrip != null) {
                        val tripId = completedTrip.id
                        val featureDto = apiResponse.toFeatureDto()

                        saveTripIfNew(threadId, tripId, featureDto)

                        emit(
                            PlanGenerationState(
                                threadId = threadId,
                                status = com.example.feature.chat.domain.model.PlanStatus.COMPLETED,
                                tripId = tripId
                            )
                        )
                    } else {
                        Timber.d(
                            "REST API returned completed itinerary, but no local completed trip found. " +
                                "Deferring to SignalR to acquire the correct Trip ID."
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "REST API fallback failed")
                val classifiedError = com.example.feature.chat.data.mapper.AiErrorMapper
                    .classifyException(e)

                val isNotFound = com.example.feature.chat.data.mapper.AiErrorMapper.extractHttpCode(e) == 404
                val isConnectionError = classifiedError == com.example.feature.chat.domain.model.AiGenerationError.AiConnectionRefused ||
                    classifiedError == com.example.feature.chat.domain.model.AiGenerationError.AiTimeout

                if (isNotFound || isConnectionError) {
                    Timber.d("Ignoring expected REST API failure on startup: $classifiedError")
                } else {
                    val cachedTrips = tripRepository.getTripsForThread(threadId)
                    val completedTrip = cachedTrips.find {
                        it.status == TripPlanStatus.COMPLETED
                    }

                    if (completedTrip != null) {
                        Timber.d("REST API failed, but cached trip found: ${completedTrip.id}")
                        emit(
                            PlanGenerationState(
                                threadId = threadId,
                                status = com.example.feature.chat.domain.model.PlanStatus.COMPLETED,
                                tripId = completedTrip.id
                            )
                        )
                        return@flow
                    }

                    Timber.d("No cached trip fallback. Emitting error: $classifiedError")
                    emit(
                        PlanGenerationState(
                            threadId = threadId,
                            status = com.example.feature.chat.domain.model.PlanStatus.FAILED,
                            error = classifiedError
                        )
                    )
                }
            }

            signalRFlow.collect { emit(it) }
        }
    }

    override suspend fun getThreadHistory(threadId: String): Result<List<ChatMessage>, DataError> {
        return Result.Success(emptyList())
    }

    override suspend fun connect() {
        signalRService.connect()
    }

    override fun observeStatus(): Flow<String> {
        return signalRService.observeStatus()
    }

    private suspend fun saveTripIfNew(
        threadId: String,
        tripId: String,
        data: com.example.feature.chat.data.remote.dto.AiStatusResponseDto
    ) {
        val existingTrips = tripRepository.getTripsForThread(threadId)
        val completedTrip = existingTrips.find { it.status == TripPlanStatus.COMPLETED }
        val hasIncomplete = completedTrip != null && completedTrip.dailyPlans.any { day ->
            day.activities.any { act -> act.type.isBlank() || act.placeName.isBlank() || act.imageUrl == null }
        }

        if (completedTrip != null && !hasIncomplete) {
            Timber.d("Trip for thread $threadId already completed and has full details, skipping save.")
            return
        }

        val targetTripId = completedTrip?.id ?: tripId
        Timber.d("Saving completed trip for thread $threadId, tripId: $targetTripId")
        val firstHotelImage = data.recommendedHotels?.firstOrNull()?.imageUrl
        val firstActivityImage = data.itinerary?.firstOrNull()?.activities?.firstOrNull()?.imageUrl
        val coverImage = firstActivityImage ?: firstHotelImage

        val tripPlan = TripPlan(
            id = targetTripId,
            threadId = threadId,
            title = data.itinerary?.firstOrNull()?.theme ?: "Generated Trip",
            createdAt = System.currentTimeMillis(),
            coverImage = coverImage,
            status = TripPlanStatus.COMPLETED,
            recommendedHotels = data.recommendedHotels?.map { h ->
                Hotel(
                    name = h.name ?: "",
                    description = h.description,
                    rating = h.rating,
                    address = h.address,
                    link = h.link,
                    imageUrl = h.imageUrl
                )
            } ?: emptyList(),
            dailyPlans = data.itinerary?.map { d ->
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
            } ?: emptyList()
        )
        tripRepository.saveTripPlan(tripPlan)
    }
}
