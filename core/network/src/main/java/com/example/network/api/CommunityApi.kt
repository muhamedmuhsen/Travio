package com.example.network.api

import com.example.common.baseresponse.BaseResponse
import com.example.network.dto.community.CommentContentRequest
import com.example.network.dto.community.LikePostResponse
import com.example.network.dto.community.PostContentRequest
import com.example.network.dto.community.PostCreationResponse
import com.example.network.dto.community.PostDto
import com.example.network.dto.community.PostWithCommentsDto
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
    suspend fun getPostById(@Path("postId") postId: Int): BaseResponse<PostWithCommentsDto>

    @POST("Community/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Int,
        @Body() request: CommentContentRequest
    ): BaseResponse<Unit>

    @POST("Community/posts/{postId}/toggle-like")
    suspend fun likePost(@Path("postId") postId: Int): LikePostResponse

    @DELETE("Community/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int)

    @DELETE("Community/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): BaseResponse<Boolean>

    @Multipart
    @POST("Community/posts/{postId}/images")
    suspend fun uploadPostImages(
        @Path("postId") postId: Int,
        @Part Images: List<MultipartBody.Part>
    )
}
