package com.example.network.api

import com.example.common.baseresponse.BaseResponse
import com.example.network.dto.review.ReviewDto
import com.example.network.dto.review.SubmitReviewRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewsApi {
    @GET("Reviews/{destinationId}")
    suspend fun getReviewsByDestinationId(@Path("destinationId") destinationId: Int): BaseResponse<List<ReviewDto>>

    @POST("Reviews")
    suspend fun submitReview(@Body request: SubmitReviewRequest): BaseResponse<Unit>
}
