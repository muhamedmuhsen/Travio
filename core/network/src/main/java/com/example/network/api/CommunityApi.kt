package com.example.network.api

import com.example.common.baseresponse.BaseResponse
import com.example.network.dto.community.CommentContentRequest
import com.example.network.dto.community.PostContentRequest
import com.example.network.dto.community.PostCreationResponse
import com.example.network.dto.community.PostDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CommunityApi {
    @POST("Community/create-post")
    suspend fun createPost(@Body request: PostContentRequest): BaseResponse<PostCreationResponse>

    @GET("Community/feed")
    suspend fun getAllPosts(): BaseResponse<List<PostDto>>

    @GET("Community/posts/{postId}")
    suspend fun getPostById(@Path("postId") postId: Int): PostDto

    @POST("community/add-comment")
    suspend fun addComment(@Body() request: CommentContentRequest)

    @POST("community/like-post/{id}")
    suspend fun likePost(@Path("id") postId: Int)

    @POST("community/unlike-post/{id}")
    suspend fun unlikePost(@Path("id") postId: Int)

    @DELETE("Community/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int)

    @Multipart
    @POST("Community/posts/{postId}/images")
    suspend fun uploadPostImages(
        @Path("postId") postId: Int,
        @Part image: List<MultipartBody.Part>
    )
}
