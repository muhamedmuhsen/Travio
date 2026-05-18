package com.example.feature.chat.data.repository

import com.example.domain.utils.DataError
import com.example.domain.utils.Result
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SignalRChatRepositoryImpl @Inject constructor(
    private val signalRService: SignalRService,
    private val tripRepository: TripRepository,
    private val aiApi: AiApi
) : ChatRepository {

    private val repositoryScope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO
    )

    init {
        repositoryScope.launch {
            signalRService.observePlanStatus().collect { status ->
                Timber.d("SignalR plan status event collected in Repository: $status")
                if (status.isCompleted && status.data != null && status.tripId != null) {
                    Timber.d("SignalR plan completed, saving tripPlan to database: $status")
                    val firstHotelImage = status.data.recommendedHotels?.firstOrNull()?.imageUrl
                    val firstActivityImage = status.data.itinerary?.firstOrNull()?.activities?.firstOrNull()?.imageUrl
                    val coverImage = firstActivityImage ?: firstHotelImage

                    val tripPlan = TripPlan(
                        id = status.tripId,
                        threadId = status.threadId,
                        title = status.data.itinerary?.firstOrNull()?.theme ?: "Generated Trip",
                        createdAt = System.currentTimeMillis(),
                        coverImage = coverImage,
                        status = TripPlanStatus.COMPLETED,
                        recommendedHotels = status.data.recommendedHotels?.map { h ->
                            Hotel(
                                name = h.name ?: "",
                                description = h.description,
                                rating = h.rating,
                                address = h.address,
                                link = h.link,
                                imageUrl = h.imageUrl
                            )
                        } ?: emptyList(),
                        dailyPlans = status.data.itinerary?.map { d ->
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

        return kotlinx.coroutines.flow.flow {
            try {
                val apiResponse = aiApi.getAiStatus(threadId)
                Timber.d(
                    "REST API getAiStatus response: status=${apiResponse.status}, message=${apiResponse.message}, data=${apiResponse.data}"
                )
                val itinerary = apiResponse.data?.itinerary
                if (!itinerary.isNullOrEmpty()) {
                    Timber.d("Retrieved itinerary with ${itinerary.size} days: $itinerary")
                    val tripId = java.util.UUID.randomUUID().toString()
                    val featureDto = apiResponse.toFeatureDto()

                    val firstHotelImage = featureDto.recommendedHotels?.firstOrNull()?.imageUrl
                    val firstActivityImage = featureDto.itinerary?.firstOrNull()?.activities?.firstOrNull()?.imageUrl
                    val coverImage = firstActivityImage ?: firstHotelImage

                    val tripPlan = TripPlan(
                        id = tripId,
                        threadId = threadId,
                        title = featureDto.itinerary?.firstOrNull()?.theme ?: "Generated Trip",
                        createdAt = System.currentTimeMillis(),
                        coverImage = coverImage,
                        status = TripPlanStatus.COMPLETED,
                        recommendedHotels = featureDto.recommendedHotels?.map { h ->
                            Hotel(
                                name = h.name ?: "",
                                description = h.description,
                                rating = h.rating,
                                address = h.address,
                                link = h.link,
                                imageUrl = h.imageUrl
                            )
                        } ?: emptyList(),
                        dailyPlans = featureDto.itinerary?.map { d ->
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

                    emit(
                        PlanGenerationState(
                            threadId = threadId,
                            status = com.example.feature.chat.domain.model.PlanStatus.COMPLETED,
                            tripId = tripId,
                            data = featureDto
                        )
                    )
                }
            } catch (e: Exception) {
                // Ignore API errors and fallback to SignalR Flow
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
}
