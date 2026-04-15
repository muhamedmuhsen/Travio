package com.example.network.api

import com.example.network.dto.favorite.FavoritesPageDto
import com.example.network.dto.favorite.FavoritesResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoritesApi {
    @GET("Favorites")
    suspend fun getFavorites(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): FavoritesResponseDto<FavoritesPageDto>

    @POST("Favorites/{destinationId}")
    suspend fun addFavorite(@Path("destinationId") destinationId: Int): FavoritesResponseDto<Boolean>

    @DELETE("Favorites/{destinationId}")
    suspend fun removeFavorite(@Path("destinationId") destinationId: Int): FavoritesResponseDto<Boolean>
}
