package com.example.network.api

import com.example.network.dto.review.ReviewSubmitResponseWrapper
import com.example.network.dto.review.ReviewsResponseDto
import com.example.network.dto.review.SubmitReviewRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewsApi {
    @GET("Destinations/{destinationId}/reviews")
    suspend fun getReviewsByDestinationId(
        @Path("destinationId") destinationId: Int,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): ReviewsResponseDto

    @POST("Destinations/{destinationId}/reviews")
    suspend fun submitReview(
        @Path("destinationId") destinationId: Int,
        @Body request: SubmitReviewRequest
    ): ReviewSubmitResponseWrapper

    @DELETE("Destinations/{destinationId}/reviews/me")
    suspend fun deleteReview(@Path("destinationId") destinationId: Int)
}
